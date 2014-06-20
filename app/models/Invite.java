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
import static com.avaje.ebean.Expr.or;

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
                    eq("practical.id", practical.getId()),
                    and(
                            eq("sender.id", sender.getId()),
                            eq("receiver.id", receiver.getId())
                    )
            )
            .findUnique();
    }

    /**
     * Find an invite by a practical and a sender
     * @param practical The practical to find the invites for
     * @param sender The sender of the invite
     * @return The list of invites send in this practical by the user
     */
    public static List<Invite> findByPracticalSender(Practical practical, User sender) {
        return find.where()
                .and(
                        eq("practical.id", practical.getId()),
                        eq("sender.id", sender.getId())
                )
                .findList();
    }

    /**
     * Find an invite by a practical and a receiver
     * @param practical The practical to find the invites for
     * @param receiver The receiver of the invite
     * @return The list of invites received in this practical by the user
     */
    public static List<Invite> findByPracticalReceiver(Practical practical, User receiver) {
        return find.where()
                .and(
                        eq("practical.id", practical.getId()),
                        eq("receiver.id", receiver.getId())
                )
                .findList();
    }

    /**
     * Method to find the pending invites a specific user has
     * @param user to find pending invites off
     * @param practical that the invite are attached to
     * @return List of invites that are pending for this user
     */
    public static List<Invite> findPendingInvitesByUser(Practical practical, User user) {
        // Get invites that have state pending and are sent by the user
        List<Invite> pendingInvites =
            find.where()
                .and(
                        and(
                                eq("state", State.Pending),
                                eq("practical.id", practical.getId())
                        ),
                        or(
                                eq("sender.id", user.getId()),
                                eq("receiver.id", user.getId())
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
     * @param sender of the new invite
     * @param receiver of the new invite
     * @return Resembles the success/failure of the check
     */
    public static Invite sendInvite(PracticalGroup sender, PracticalGroup receiver) {
        // Check whether the sender has not already send an invite to the receiver and
        // Check whether the receiver has not already send an invite to the receiver and
        // Check whether the amount of send invitations does not exceed the set maximum
        if(!canInvite(sender, receiver)
                || findPendingInvitesByUser(sender.getPractical(), sender.getOwner()).size() > INVITES_MAX) {
            return null;
        }
        Invite newInvite = new Invite(sender.getPractical(), sender.getOwner(), receiver.getOwner());
        newInvite.save();
        return newInvite;
    }

    /**
     * Check if an invite can be send between two practical groups
     * @param group1 The first practical group
     * @param group2 The second practical group
     * @return If two groups can invite each other
     */
    public static boolean canInvite(PracticalGroup group1, PracticalGroup group2) {
        // Check if they are not sending an invite to themself
        if(group1.equals(group2)) {
            return false;
        }

        // Check whether the groups have already send an invite to eachother
        Invite invite = find.where()
                .and(
                        eq("practical.id", group1.getPractical().getId()),
                        or(
                                and(
                                        eq("sender.id", group1.getOwner().getId()),
                                        eq("receiver.id", group2.getOwner().getId())
                                ),
                                and(
                                        eq("sender.id", group2.getOwner().getId()),
                                        eq("receiver.id", group1.getOwner().getId())
                                )
                        )
                ).findUnique();
        return (invite == null);
    }

    /**
     * Gets the invite identifier
     * @return the identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the practical that is linked to the invite
     * @return the practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Sets the practical that is linked to the invite
     * @param practical The practical
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }

    /**
     * Gets the sender of the invite
     * @return the sender of the invite
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender of the invite
     * @param sender the user that sends the invite
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Gets the receiver of the invite
     * @return the receiver of the invite
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver of the invite
     * @param receiver that sends the invite
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
