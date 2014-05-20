package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class representation of the database table invite
 */
@Entity
@SuppressWarnings("serial")
public class Invite extends Model {

    /**
     * Id of the invite
     */
    @Id
    private long id;

    /**
     * Boolean which indicates whether the invitation has been accepted
     */
    @Constraints.Required
    private boolean accepted;

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
     * Constructor of an invite
     * @param practical of the new invite
     * @param sender of the new invite
     * @param receiver of the new invite
     */
    public Invite(Practical practical, User sender, User receiver) {
        this.accepted = false;
        this.practical = practical;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Method to send an invite, the method checks whether the invite has not already been send
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
     * Getter for accepted
     * @return accepted
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Setter for accepted
     * @param accepted to set
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
