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
     * The first name of an user
     */
    private String firstName;

    /**
     * The last name of an user
     */
    private String lastName;

    /**
     * The language of an user
     */
    private String language;

    /**
     * The login name of an user
     */
    private String loginName;

    /**
     * The email adres of an user
     */
    private String email;

    /**
     * Get the user id
     * @return The id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the user id
     * @param id The id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieve the name of the user
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Create a new User with a certain name
     * @param name The name of the User
     */
    public User(String name) {
     this.name = name;
    }

    /**
     * Define the finder for Ebean
     */
    public static Finder<Long, User> find = new Finder<Long, User>(
        Long.class, User.class
    );

    /**
     * Checks the user authentication with email and password
     * @param email The user email address
     * @param password The user password
     * @return True if user authenticated successfully else false
     */
    public static User authenticate(String email, String password) {
        Identity identity = PasswordIdentity.authenticate(email, password);

        // Check if the identity is found using the email, password authentication
        if(identity != null) {
            return identity.getUser();
        }

        return null;
    }
}
