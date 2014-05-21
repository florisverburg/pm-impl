package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.EnumValue;
import play.Logger;
import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.eq;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class representation of the database table invite
 */
@Entity
@SuppressWarnings("serial")
public class Invite extends Model {

    /**
     * The different types the user can be
     */
    public enum State {
        /**
         * The state accepted
         */
        @EnumValue("ACCEPTED")
        ACCEPTED,
        /**
         * The state pending
         */
        @EnumValue("PENDING")
        PENDING,
        /**
         * The state rejected.
         */
        @EnumValue("REJECTED")
        REJECTED
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

    public static Model.Finder<Long, Invite> find =
            new Finder<Long, Invite>(Long.class, Invite.class);

    public static Invite findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * Method to find the pending invites an user has
     * @return list of pending invites
     */
    public static List<Invite> findPendingInvites() {
        List<Invite> list = find.where().eq("state", State.PENDING).findList();
        for (int i = 0; i < list.size(); i++) {
            Logger.debug("pendingInvites:" + list.get(i).getId() + " " + list.get(i).getSender().getFirstName());
        }
        return list;
    }

    /**
     * Method to find the pending invites a specific user has
     */
    public static List<Invite> findPendingUserInvites(User user) {
        // Get invites that have state pending and are sent by the user
        List<Invite> pendingSentInvites =
                find.where()
                    .eq("senderId", user.getId())
                    .eq("state", State.PENDING)
                    .findList();
        //Get invites that have state pending and are received by the user
        List<Invite> pendingReceivedInvites =
                find.where()
                    .eq("receiverId", user.getId())
                    .eq("state", State.PENDING)
                    .findList();
        List<Invite> combinedList = new ArrayList<Invite>();
        combinedList.addAll(pendingSentInvites);
        combinedList.addAll(pendingReceivedInvites);
        return combinedList;
    }

    /**
     * Constructor of an invite
     * @param practical of the new invite
     * @param sender of the new invite
     * @param receiver of the new invite
     */
    public Invite(Practical practical, User sender, User receiver) {
        this.state = State.PENDING;
        this.practical = practical;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Accept the invite and rejects all other received invites
     * @param invite to accept
     */
    public static void acceptInvite(Invite invite) {
        User receiver = User.findById (invite.getReceiver().getId());
        User sender = User.findById(invite.getSender().getId());

        // Reject all pending of the sender and receiver invites
        for(Invite pendingInvite : findPendingUserInvites(receiver)) {
            pendingInvite.setState(State.REJECTED);
            pendingInvite.save();
        }
        // Accept the invite
        invite = Invite.findById(invite.getId());
        invite.setState(State.ACCEPTED);
        invite.save();

        // Delete practical group of receiver
        PracticalGroup receiversPracticalGroup = PracticalGroup.findPracticalGroupWithPracticalAndUser(invite.getPractical(), receiver);
        // Delete practical group from user
        receiver.removePracticalGroup(receiversPracticalGroup);
        receiver.save();
        // Delete practical group from practical
        invite.getPractical().removePracticalGroup(receiversPracticalGroup);
        invite.getPractical().save();
        Ebean.delete(receiversPracticalGroup);


        // Add receiver to practical group of sender
        PracticalGroup sendersPracticalGroup = PracticalGroup.findPracticalGroupWithPracticalAndUser(invite.getPractical(), sender);
        sendersPracticalGroup.addUser(receiver);
        sendersPracticalGroup.save();
    }

    /**
     * Method to send an invite, the method checks whether the invite has not already been send and the amount of pending invites send has not exceeded
     * @param practical which the invite is for
     * @param sender of the new invite
     * @param receiver of the new invite
     * @return
     */
    public static boolean sendInvite(Practical practical, User sender, User receiver) {
        // Check whether the sender has not already send an invite to the receiver
        if(!checkInvite(sender, receiver)) {
            return false;
        }
        // Check whether the receiver has not already send an invite to the receiver
        if(!checkInvite(receiver, sender)) {
            return false;
        }
        if (findPendingInvites().size() > 5) {
            return false;
        }
        Invite newInvite = new Invite(practical, sender, receiver);
        newInvite.save();
        return true;
    }

    /**
     * Returns true if the first user has not send an invite to the second user
     * @param sender first user
     * @param receiver second user
     * @return
     */
    private static boolean checkInvite(User sender, User receiver) {
        // Check whether the sender has not already send an invite to the receiver
        for (Invite invite : sender.getInvitesSend()) {
            if(invite.getReceiver().getId().equals(receiver.getId())) {
                return false;
            }
        }
        return true;
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
