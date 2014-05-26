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
    @JoinColumn(name = "practicalId")
    private Practical practical;

    /**
     * User that send the invite
     */
    @ManyToOne
    @JoinColumn(name = "senderId")
    private User sender;

    /**
     * User that received the invite
     */
    @ManyToOne
    @JoinColumn(name = "receiverId")
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
        // Reject all pending of the sender and receiver invites
        String updStatement = "update invite set state = :st where receiverId = :rcvId or senderId = :sndrId";
        Update<Invite> update = Ebean.createUpdate(Invite.class, updStatement);
        update.set("st", State.Rejected);
        update.set("rcvId", receiver.getId());
        update.set("sndrId", sender.getId());
        update.execute();
        // Accept the invite
        state = State.Accepted;
        this.save();
        // Delete practical group of receiver
        PracticalGroup receiversPracticalGroup =
                PracticalGroup.findWithPracticalAndUser(practical, receiver);
        Ebean.delete(receiversPracticalGroup);
        // Add receiver to practical group of sender
        PracticalGroup sendersPracticalGroup =
                PracticalGroup.findWithPracticalAndUser(practical, sender);
        sendersPracticalGroup.addGroupMember(receiver);
        sendersPracticalGroup.save();
    }

    /**
     * Method to reject an invite
     * @param user that wants to reject the invite
     */
    public void reject(User user) {
        PracticalGroup practicalGroupOfRejecter =
                PracticalGroup.findWithPracticalAndUser(practical, user);

        // Only remove the rejecting user from the group and update the group
        practicalGroupOfRejecter.removeUser(receiver);
        practicalGroupOfRejecter.save();
        // Create a new practical group for the removed user
        PracticalGroup newPracticalGroup = new PracticalGroup(practical, user);
        newPracticalGroup.save();
        state = State.Rejected;
        this.save();
    }

    /**
     * Method to withdraw invite
     */
    public void withdraw() {
        state = State.Withdrawn;
        this.save();
    }

    /**
     * Method to resend invite
     */
    public void resend() {
        state = State.Pending;
        this.save();
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
        if(!checkInvite(sender, receiver)
                || !checkInvite(receiver, sender)
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
     * @return Resembles the success/failure of the check
     */
    private static boolean checkInvite(User sender, User receiver) {
        // Check whether the sender has not already send an invite to the receiver
        Invite alreadySentInvite = find.where()
                .and(
                    eq("senderId", sender.getId()),
                    eq("receiverId", receiver.getId()))
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
