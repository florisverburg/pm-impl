package models;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import helpers.MD5;
import play.data.validation.*;
import play.db.ebean.*;

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * Created by Freek on 09/05/14.
 * This is the user representation of the database
 */
@Entity
@SuppressWarnings("serial")
public class User extends Model {

    /**
     * The amount of random bits that needs to be generated for the token
     * */
    private static final int TOKEN_RANDOM_BITS = 130;

    /**
     * The base number of the random token generated number
     */
    private static final int TOKEN_RANDOM_BASE = 16;

    /**
     * The Gravatar avatar URL
     */
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";

    /**
     * The different types the user can be
     */
    public enum Type {
        /**
         * The Admin.
         */
        @EnumValue("Admin")
        Admin,
        /**
         * The Teacher.
         */
        @EnumValue("Teacher")
        Teacher,
        /**
         * The User.
         */
        @EnumValue("User")
        User,
        /**
         * The Guest.
         */
        @EnumValue("Guest")
        Guest
    }

    /**
     * The enum Profile image.
     */
    public enum ProfileImage {
        /**
         * Use Gravatar as profile image
         */
        @EnumValue("Gravatar")
        Gravatar,
        /**
         * Use no profile image
         */
        @EnumValue("None")
        None
    }

    /**
     * The user identifier
     */
    @Id
    private Long id;

    /**
     * The first name of an user
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String firstName;

    /**
     * The last name of an user
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String lastName;

    /**
     * The email address of an user
     */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * The type of user (also named group)
     */
    @Constraints.Required
    @Constraints.Min(1)
    private Type type;

    /**
     * The token for the email validation
     */
    private String token;

    /**
     * Some simple profile text which is viewable to everyone in the practical
     */
    @Constraints.MaxLength(255)
    private String profileText;

    /**
     * The link to the profile image if provided
     */
    @Constraints.Required
    private ProfileImage profileImage;

    /**
     * Finder to be defined to use the many-to-many relationship of user and skill
     */
    public static Model.Finder<Long, User> find =
            new Finder<Long, User>(Long.class, User.class);

    /**
     * Constructor for the User class
     * @param firstName firstName of the user
     * @param lastName lastName of the user
     * @param email email of the user
     * @param type The type of user
     */
    public User(String firstName, String lastName, String email, Type type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
        this.token = generateSecret();
        this.profileImage = ProfileImage.None;
    }

    /**
     * Constructor for the User class with default type User
     * @param firstName firstName of the user
     * @param lastName lastName of the user
     * @param email email of the user
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = Type.User;
        this.token = generateSecret();
        this.profileImage = ProfileImage.None;
    }

    /**
     * Method that returns a random generated secret
     * @return random generated secret
     */
    public String generateSecret() {
        return new BigInteger(TOKEN_RANDOM_BITS, new SecureRandom()).toString(TOKEN_RANDOM_BASE);
    }

    /**
     * Get an user by email address
     * @param email The email address
     * @return The user if found, else null
     */
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * Get an user by id
     * @param id The user id
     * @return The user if found, else null
     */
    public static User findById(Long id) {
        return find.byId(id);
    }

    /**
     * Get the user id
     * @return The id
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets type.
     * @return The type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type.
     * @param type The type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Method to search a User by name
     * @param name that will be used to search for
     * @return returns the User that has a name equal to the input
     */
    public static User findByName(String name) {
        return find.where().eq("firstName", name).findUnique();
    }

    /**
     * Getter of the firstName
     * @return firstName first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter of the firstName
     * @param firstName to be set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter of the lastName
     * @return lastName last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter of the lastName
     * @param lastName to be set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter of the email address
     * @return email Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of the email address
     * @param email Email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the users' full name
     * @return The full name
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Gets the profile text.
     * @return The profile text
     */
    public String getProfileText() {
        return profileText;
    }

    /**
     * Sets the profile text.
     * @param profileText The profile text
     */
    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    /**
     * Gets the profile image.
     * @return The profile image
     */
    public ProfileImage getProfileImage() {
        return profileImage;
    }

    /**
     * Sets the profile image.
     * @param profileImage The profile image
     */
    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Gets the profile image or Gravatar url.
     * @return The profile image url
     */
    public String getProfileImageUrl() {
        switch(this.profileImage) {
            case Gravatar:
                return GRAVATAR_URL + MD5.crypt(this.getEmail().trim().toLowerCase());
            default:
                // use gravatar with forcing the default image (f=y)
                return GRAVATAR_URL + MD5.crypt(this.getEmail().trim().toLowerCase())
                        + "?d=identicon&f=y";
        }
    }

    /**
     * Getter of the token
     * @return token token
     */
    public String getToken() {
        return token;
    }

    /**
     * Setter of the token
     * @param token to be set
     */
    public void setToken(String token) {
        this.token = token;
    }
}
