package models;

import play.data.validation.*;
import play.db.ebean.*;
import javax.persistence.*;

/**
 * Created by Freek on 12/05/14.
 * The Identity is a login method for the User
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="type")
@SuppressWarnings("serial")
public abstract class Identity extends Model {

    /**
     * The identity identifier
     */
    @Id
    @Constraints.Required
    protected Long id;

    /**
     * The user which the identity is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected User user;

    /**
     * Get the identity id
     * @return The id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the identity id
     * @param id The id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the user linked to the identity
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * Link the user to the identity
     * @param user The user to link
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Define the finder for Ebean
     */
    public static Finder<Long, Identity> find = new Finder<Long, Identity>(
            Long.class, Identity.class
    );
}
