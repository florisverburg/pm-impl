package models;

import play.db.ebean.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Floris on 02/06/14.
 * Class representation of the database table message.
 */
@Entity
public class Message extends Model implements Comparable<Message> {

    /**
     * Id of the invite
     */
    @Id
    private long id;

    /**
     * The timestamp
     */
    private Timestamp timestamp;

    /**
     * Many to one relation with the invite.
     */
    @ManyToOne
    private Invite invite;

    /**
     * User that sends the message
     */
    @ManyToOne
    private User user;

    /**
     * The message
     */
    private String message;

    /**
     * Finder of the message
     */
    public static Model.Finder<Long, Message> find =
            new Model.Finder<Long, Message>(Long.class, Message.class);

    /**
     * Constructor of the message.
     * @param invite the invite where the message is sent to
     * @param user the user who has sent the message
     * @param message the message
     */
    public Message(Invite invite, User user, String message) {
        java.util.Date date= new java.util.Date();
        this.invite = invite;
        this.user = user;
        this.message = message;
        this.timestamp = new Timestamp(date.getTime());
    }

    /**
     * Getter for the id.
     * @return the id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Getter for the timestamp.
     * @return the timestamp
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    /**
     * Setter for the timestamp
     * @param timestamp the timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for the invite.
     * @return the the invite
     */
    public Invite getInvite() {
        return this.invite;
    }

    /**
     * Setter for the invite
     * @param invite the invite
     */
    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    /**
     * Getter for the user.
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Setter for the user
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter for the message.
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for the message
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Compare to for the message.
     * @param that the message to compare to
     * @return a negative int if this < that, 0 if this == that, positive int if this > that
     */
    @Override
    public int compareTo(Message that) {
        return this.getTimestamp().compareTo(that.getTimestamp());
    }

    /**
     * Get the time in normal format
     * @return the time
     */
    public String getTime() {
        String time = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(this.getTimestamp());
        return time;
    }

    /**
     * Finds the messages of a specific invite ordered by timestamp
     * @param invite The invite to get the messages for
     * @return The ordered list of messages
     */
    public static List<Message> findByInvite(Invite invite) {
        return find.where().eq("invite.id", invite.getId()).orderBy("timestamp").findList();
    }
}
