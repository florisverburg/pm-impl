package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class representation of the database table invite
 */
@Entity
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
}
