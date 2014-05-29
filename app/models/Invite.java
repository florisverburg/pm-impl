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
                eq("practicalId", practical.getId()),
                and(
                        eq("senderId", sender.getId()),
                        eq("receiverId", receiver.getId())
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
<<<<<<< HEAD
                find.where()
                    .or(
                            and(
                                    and(
                                            eq("sender.id", user.getId()),
                                            eq("practical.id", practical.getId())
                                    ),
                                    eq("state", State.Pending)
                            ),
                            and(
                                    and(
                                            eq("receiver.id", user.getId()),
                                            eq("practical.id", practical.getId())
                                    ),
                                    eq("state", State.Pending)
                            )
=======
            find.where()
                .or(
                    and(
                        and(
                                eq("senderId", user.getId()),
                                eq("practicalId", practical.getId())
                        ),
                        eq("state", State.Pending)
                    ),
                    and(
                        and(
                                eq("receiverId", user.getId()),
                                eq("practicalId", practical.getId())
                        ),
                        eq("state", State.Pending)
>>>>>>> Added some more funtionality that was still missing for the correct accepting/rejecting/... of invites
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
        rejectOtherInvitesUser(this.getReceiver(), true);
        rejectOtherInvitesUser(this.getSender(), false);
        this.refresh();

        // Delete practical group of receiver
        PracticalGroup receiversPracticalGroup = PracticalGroup.findWithPracticalAndUser(practical, receiver);
        Ebean.delete(receiversPracticalGroup);
        // Add receiver to practical group of sender
        PracticalGroup sendersPracticalGroup =
                PracticalGroup.findWithPracticalAndUser(practical, sender);
        sendersPracticalGroup.addGroupMember(receiver);
        sendersPracticalGroup.save();
    }

    /**
     * @param user that wants to reject the invite
     * @param include whether or not to include that also the send invites should be rejected
     */
    private void rejectOtherInvitesUser(User user, boolean include) {
        String updStatement = "update invite set state = :st1 "
<<<<<<< HEAD
                + "where "
                + "(practical_id = :prctId "
                + "and "
                + "(receiver_id = :rcvId1 ";
=======
            + "where "
            + "( practicalId = :prctId "
            + "and "
            + "(receiverId = :rcvId1 ";
>>>>>>> Added some more funtionality that was still missing for the correct accepting/rejecting/... of invites
        if(include) {
            updStatement = updStatement + "or sender_id = :rcvId2 ";
        }
        updStatement = updStatement + ") and state = :st2 )";
        Update<Invite> update = Ebean.createUpdate(Invite.class, updStatement);
        update.set("st1", State.Rejected);
        update.set("st2", State.Pending);
        update.set("rcvId1", user.getId());
        update.set("rcvId2", user.getId());
        update.set("prctId", this.practical.getId());
        update.execute();
    }

    /**
     * Method to reject an invite
     * @param user that wants to reject the invite
     */
    public void reject(User user) {
        if(!this.state.equals(State.Accepted)) {
            return;
        }
        PracticalGroup practicalGroupOfRejecter =
                PracticalGroup.findWithPracticalAndUser(practical, user);

        // Only remove the receiver of the invite from the group and update the group
        practicalGroupOfRejecter.removeGroupMember(receiver);
        practicalGroupOfRejecter.save();
        // Create a new practical group for the removed user
        PracticalGroup newPracticalGroup = new PracticalGroup(practical, receiver);
        newPracticalGroup.save();
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
    public static void resend(User user, Invite invite) {
        if(!invite.state.equals(State.Withdrawn) && !invite.state.equals(State.Rejected)) {
            return;
        }
        if(invite.sender.equals(user)) {
            invite.setState(State.Pending);
            invite.save();
            return;
        }
        // If the receiver of the original invite wants to resend it, create a new invite from him to the user
        Invite newInvite = new Invite(invite.practical, invite.receiver, invite.sender);
        newInvite.save();
        Ebean.delete(invite);
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
