package models;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.*;

/**
 * Created by Freek on 09/05/14.
 * This is the user representation of the database
 */
@Entity
public class User extends Model {

    /**
     * The user identifier
     */
    @Id
    private Long id;

    /**
     * The user name
     */
    @Constraints.Required
    private String name;

    /**
     * Retrieve the name of the user
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Create a new User with a certain name
     * @param name The name of the User
     */
    public User(String name) {
     this.name = name;
    }

    /**
     * Checks the user authentication with email and password
     * @param email The user email address
     * @param password The user password
     * @return True if user authenticated successfully else false
     */
    public static User authenticate(String email, String password) {
        Identity identity = Identity.authenticate(email, password);

        // Check if the identity is found using the email, password authentication
        if(identity != null) {
            return identity.getUser();
        }

        return null;
    }
}
