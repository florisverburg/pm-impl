package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.EnumValue;
import org.hibernate.validator.constraints.Length;
import play.data.validation.*;
import play.db.ebean.*;
import javax.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Freek on 12/05/14.
 * The Identity is a login method for the User
 */
@Entity
public class Identity extends Model {
    /**
     * The type of identities
     */
    public enum Type {
        @EnumValue("E")
        EMAIL,
        @EnumValue("L")
        LINKEDIN
    }

    /**
     * The identity identifier
     */
    @Id
    @Constraints.Required
    private Long id;

    /**
     * The identity type
     */
    @Constraints.Required
    private Type type;

    /**
     * The user which the identity is linked to
     */
    @ManyToOne
    @Constraints.Required
    private User user;

    /**
     * Identifier of the identity
     * Depending on the type of identity the data differs:
     * - EMAIL: The email address
     */
    @Constraints.Required
    private String identifier;

    /**
     * The data of the identity.
     * Depending on the type of the identity the data differs:
     * - EMAIL: It contains the hashed password
     */
    @Length(max=250)
    @Constraints.Required
    private String data;

    /**
     * Get the user from the identity
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * Create an identity with an username and password
     * @param user The user for which the identity needs to be created
     * @param email The email address used for the identity
     * @param password The plaintext password for the identity
     * @return A new identity based on the provided login information
     */
    public Identity(User user, String email, String password) {
        this.type = Type.EMAIL;
        this.user = user;
        this.identifier = email;
        this.data = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks the identity authentication with email and password
     * @param email The identity email address
     * @param password The identity password
     * @return The identity if it is correct authentication else null
     */
    public static Identity authenticate(String email, String password) {
        Identity identity = Ebean.find(Identity.class)
                .where()
                .eq("type", Type.EMAIL)
                .eq("identifier", email)
                .findUnique();

        // When the identity exists check the password
        if(identity != null && BCrypt.checkpw(password, identity.data))
            return identity;

        return null;
    }
}
