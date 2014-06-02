package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Update;
import com.avaje.ebean.annotation.EnumValue;
import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;
import java.util.List;

import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class representation of the database table invite
 */
@Entity
@SuppressWarnings("serial")
public class Invite extends Model {

    /**
     * Maximal amount of invites a user can send
     */
    private static final int INVITES_MAX = 5;

    /**
     * The different types the user can be
     */
    public enum State {
        /**
         * The state accepted
         */
        @EnumValue("Accepted")
        Accepted,
        /**
         * The state pending
         */
        @EnumValue("Pending")
        Pending,
        /**
         * The state rejected.
         */
        @EnumValue("Rejected")
        Rejected,
        /**
         * The state withdrawn
         */
        @EnumValue("Withdrawn")
        Withdrawn
    }

    /**
     * Id of the invite
     */
    @Id
    private long id;

    /**
     * The state of the invite
     */
    @Constraints.Required
    @Constraints.Min(1)
    private State state;

    /**
     * Practical which this invite belongs to
     */
    @ManyToOne
    private Practical practical;

    /**
     * User that send the invite
     */
    @ManyToOne
    private User sender;

    /**
     * User that received the invite
     */
    @ManyToOne
    private User receiver;

    /**
     * Finder of the invite
     */
    public static Model.Finder<Long, Invite> find =
            new Finder<Long, Invite>(Long.class, Invite.class);

    /**
     * Method to find an invite by it's id
     * @param id of the invite to find
     * @return the appropriate invite
     */
    public static Invite findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * Method to find an invite when you do not know the id
     * @param practical of the invite
     * @param sender of the invite
     * @param receiver of the invite
     * @return the found invite
     */
    public static Invite findByPracticalSenderReceiver(Practical practical, User sender, User receiver) {
        return find.where()
            .and(
                eq("practical_id", practical.getId()),
                and(
                        eq("sender_id", sender.getId()),
                        eq("receiver_id", receiver.getId())
                )
            )
            .findUnique();
    }

    /**
     * Method to find the pending invites a specific user has
     * @param user to find pending invites off
     * @param practical that the invite are attached to
     * @return List of invites that are pending for this user
     */
    public static List<Invite> findPendingInvitesWhereUser(User user, Practical practical) {
        // Get invites that have state pending and are sent by the user
        List<Invite> pendingInvites =
            find.where()
                .or(
                    and(
                        and(
                                eq("sender_id", user.getId()),
                                eq("practical_id", practical.getId())
                        ),
                        eq("state", State.Pending)
                    ),
                    and(
                        and(
                                eq("receiver_id", user.getId()),
                                eq("practical_id", practical.getId())
                        ),
                        eq("state", State.Pending)
                    )
                )
                .findList();
        return pendingInvites;
    }

    /**
     * Constructor of an invite
     * @param practical of the new invite
     * @param sender of the new invite
     * @param receiver of the new invite
     */
    public Invite(Practical practical, User sender, User receiver) {
        this.state = State.Pending;
        this.practical = practical;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Accept the invite and rejects all other received invites
     */
    public void accept() {
        if(!state.equals(State.Pending)) {
            return;
        }
        // Accept the invite
        this.state = State.Accepted;
        this.save();
        // Reject all pending of the receiver invites
        rejectOtherInvitesUser(this.getReceiver(), this.practical, true, State.Pending);
        rejectOtherInvitesUser(this.getSender(), this.practical, false, State.Pending);
        this.refresh();

        // Delete practical group of receiver
        PracticalGroup receiversPracticalGroup = PracticalGroup.findWithPracticalAndUser(practical, receiver);
        // Add receivers to practical group of sender
        PracticalGroup sendersPracticalGroup =
                PracticalGroup.findWithPracticalAndUser(practical, sender);
        for(User groupMember : receiversPracticalGroup.getGroupMembers()) {
            sendersPracticalGroup.addGroupMember(groupMember);
        }
        Ebean.delete(receiversPracticalGroup);
        sendersPracticalGroup.save();
    }

    /**
     * @param user that wants to reject the invite
     * @param practical of the invite that needs to be updated
     * @param include whether or not to include the send invites while rejecting
     * @param formerState of the invite
     */
    public static void rejectOtherInvitesUser(User user,
        Practical practical, boolean include, Invite.State formerState) {
        String updStatement = "update invite set state = :st1 "
                + "where "
                + "(practical_id = :prctId "
                + "and "
                + "(receiver_id = :rcvId1 ";
        if(include) {
            updStatement = updStatement + "or sender_id = :rcvId2 ";
        }
        updStatement = updStatement + ") and state = :st2 )";
        Update<Invite> update = Ebean.createUpdate(Invite.class, updStatement);
        update.set("st1", State.Rejected);
        update.set("st2", formerState);
        update.set("rcvId1", user.getId());
        update.set("rcvId2", user.getId());
        update.set("prctId", practical.getId());
        update.execute();
    }

    /**
     * Method to reject an invite
     */
    public void reject() {
        if(!this.state.equals(State.Pending)) {
            return;
        }
        state = State.Rejected;
        this.save();
    }

    /**
     * Method to withdraw invite
     */
    public void withdraw() {
        if(!this.state.equals(State.Pending)) {
            return;
        }
        state = State.Withdrawn;
        this.save();
    }

    /**
     * Method to resend invite when it has been withdrawn or rejected
     * @param user that wants to resend the invite
     */
    public void resend(User user) {
        if(!state.equals(State.Withdrawn) && !state.equals(State.Rejected)) {
            return;
        }

        // Check if the sender needs to be swapped
        if(!sender.equals(user)) {
            User tempUser = receiver;
            setReceiver(this.sender);
            setSender(tempUser);
        }

        // Save the invite and set the state to pending
        setState(State.Pending);
        save();
    }

    /**
     * Method to send an invite, the method checks whether the invite has not already been
     * send and the amount of pending invites send has not exceeded
     * @param practical which the invite is for
     * @param sender of the new invite
     * @param receiver of the new invite
     * @return Resembles the success/failure of the check
     */
    public static Invite sendInvite(Practical practical, User sender, User receiver) {
        // Check whether the sender has not already send an invite to the receiver and
        // Check whether the receiver has not already send an invite to the receiver and
        // Check whether the amount of send invitations does not exceed the set maximum
        if(!checkInvite(sender, receiver, practical)
                || !checkInvite(receiver, sender, practical)
                || sender.equals(receiver)
                || sender.findPendingInvitesUser(practical).size() > INVITES_MAX) {
            return null;
        }
        Invite newInvite = new Invite(practical, sender, receiver);
        newInvite.save();
        return newInvite;
    }

    /**
     * Returns true if the first user has not send an invite to the second user
     * @param sender first user
     * @param receiver second user
     * @param practical of the invite that needs to be checked
     * @return Resembles the success/failure of the check
     */
    public static boolean checkInvite(User sender, User receiver, Practical practical) {
        // Check whether the sender has not already send an invite to the receiver
        Invite alreadySentInvite = find.where()
                .and(
                    eq("practical.id", practical.getId()),
                    and(
                        eq("sender.id", sender.getId()),
                        eq("receiver.id", receiver.getId())
                    )
                )
                .findUnique();
        return (null == alreadySentInvite);
    }

    /**
     * Getter for id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for id
     * @param id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for practical
     * @return practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Setter for practical
     * @param practical to set
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }

    /**
     * Getter for sender
     * @return sender
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setter for sender
     * @param sender to set
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Getter for receiver
     * @return receiver
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Setter for receiver
     * @param receiver to set
     */
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    /**
     * Getter of the state
     * @return state
     */
    public State getState() {
        return state;
    }

    /**
     * Setter of the state
     * @param state to set
     */
    public void setState(State state) {
        this.state = state;
    }
}
