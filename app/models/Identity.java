package models;

import play.data.validation.*;
import play.db.ebean.*;
import javax.persistence.*;
import java.util.List;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @Constraints.Required
    protected User user;

    /**
     * Get the identity id
     * @return The id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the user linked to the identity
     * @return The user
     */
    public User getUser() {
        return this.user;
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

    /**
     * Find the identities based on a user
     * @param user The user the identities are from
     * @return The list of identities
     */
    public static List<Identity> findByUser(User user) {
        return find.where().eq("user.id", user.getId()).findList();
    }
}
