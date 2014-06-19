package models;

import com.avaje.ebean.Ebean;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.*;

import javax.persistence.*;

/**
 * Created by Freek on 13/05/14.
 * This includes the Password identity authentication method
 */
@Entity
@DiscriminatorValue("Password")
@SuppressWarnings("serial")
public class PasswordIdentity extends Identity {

    /**
     * Identifier of the identity
     * For the Password Identity the identifier is an email address
     */
    @Column(name = "identifier")
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * The data of the identity.
     * For the Password Identity the data is the encrypted password
     */
    @Column(name = "data")
    @Constraints.Required
    private String password;

    /**
     * Gets the identity email address identifier.
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address identifier.
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the password and encrypts it.
     * @param password The password for the identity
     */
    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Check if the user has an password identity
     * @param user The user to search for
     * @return If the user has an password identity
     */
    public static Boolean contains(User user) {
        return Ebean.find(PasswordIdentity.class).where().eq("user.id", user.getId()).findUnique() != null;
    }

    /**
     * Create a new password identity with an username and password
     * @param user The user for which the identity needs to be created
     * @param email The email address used for the identity
     * @param password The plaintext password for the identity
     */
    public PasswordIdentity(User user, String email, String password) {
        setUser(user);
        setEmail(email);
        setPassword(password);
    }

    /**
     * Checks the identity authentication with email and password
     * @param email The identity email address
     * @param password The identity password
     * @return The identity if it is correct authentication else null
     */
    public static PasswordIdentity authenticate(String email, String password) {
        PasswordIdentity identity = Ebean.find(PasswordIdentity.class)
                .where()
                .eq("identifier", email)
                .findUnique();

        // When the identity exists check the password
        if(identity != null && BCrypt.checkpw(password, identity.password)) {
            return identity;
        }

        return null;
    }
}
