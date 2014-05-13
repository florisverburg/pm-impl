package models;

import com.avaje.ebean.Ebean;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;

/**
 * Created by Freek on 13/05/14.
 * This includes the Password identity authentication method
 */
@Entity
@DiscriminatorValue("P")
public class PasswordIdentity extends Identity {

    /**
     * Create an identity with an username and password
     *
     * @param user     The user for which the identity needs to be created
     * @param email    The email address used for the identity
     * @param password The plaintext password for the identity
     */
    public PasswordIdentity(User user, String email, String password) {
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
        Identity identity = Ebean.find(PasswordIdentity.class)
                .where()
                .eq("identifier", email)
                .findUnique();

        // When the identity exists check the password
        if(identity != null && BCrypt.checkpw(password, identity.data)) {
            return identity;
        }

        return null;
    }
}
