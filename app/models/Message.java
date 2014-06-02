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

    private Timestamp timestamp;

    @ManyToOne
    private Invite invite;

    /**
     * User that sends the message
     */
    @ManyToOne
    private User user;

    private String message;

    /**
     * Finder of the message
     */
    public static Model.Finder<Long, Invite> find =
            new Model.Finder<Long, Invite>(Long.class, Invite.class);

    public Message(Invite invite, User user, String message) {
        java.util.Date date= new java.util.Date();
        this.invite = invite;
        this.user = user;
        this.message = message;
        this.timestamp = new Timestamp(date.getTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Invite getInvite() {
        return invite;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int compareTo(Message that) {
        return this.getTimestamp().compareTo(that.getTimestamp());
    }

    public String getTime() {
        String time = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(this.getTimestamp());
        return time;
    }
}
