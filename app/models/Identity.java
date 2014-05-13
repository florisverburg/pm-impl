package models;

import org.hibernate.validator.constraints.Length;
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
     * Identifier of the identity
     * Depending on the type of identity the data differs.
     */
    @Constraints.Required
    protected String identifier;

    /**
     * The data of the identity.
     * Depending on the type of the identity the data differs.
     */
    @Length(max=250)
    @Constraints.Required
    protected String data;

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
     * Get the identifier of the identity
     * @return The identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the identifier of the identity
     * @param identifier The identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the data of the identity
     * @return The data
     */
    public String getData() {
        return data;
    }

    /**
     * Set the data of the identity
     * @param data The data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Define the finder for Ebean
     */
    public static Finder<Long, Identity> find = new Finder<Long, Identity>(
            Long.class, Identity.class
    );
}
