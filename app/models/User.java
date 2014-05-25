package models;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import helpers.MD5;
import play.Play;
import play.data.validation.*;
import play.db.ebean.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.typesafe.plugin.*;
import play.mvc.*;


/**
 * Created by Freek on 09/05/14.
 * This is the user representation of the database
 */
@Entity
@SuppressWarnings("serial")
public class User extends Model {

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
    private String profileText;

    /**
     * The link to the profile image if provided
     */
    @Constraints.Required
    private ProfileImage profileImage;

    /**
     * The identities linked to the user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Identity> identities = new ArrayList<Identity>();

    /**
     * The many-to-many relationship defined by the skills and users
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<Skill>();

    /**
     * The many-to-many relationship defined for the users and practicals
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Practical> practicals = new ArrayList<Practical>();

    /**
     * One-to-many relationship between practical and user
     */
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Practical> practicalsAdmin = new ArrayList<Practical>();

    /**
     * Many-to-many relationship defined for the users and practicalGroups
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PracticalGroup> practicalGroups = new ArrayList<PracticalGroup>();

    /**
     * The client identifier of the Linkedin API
     */
    private static final String EMAIL = Play.application().configuration().getString("email.address");

    /**
     * One-to-many relationship between user and invite (sender)
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Invite> invitesSend = new ArrayList<Invite>();

    /**
     * One-to-many relationship between user and invite (receiver)
     */
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Invite> invitesReceived = new ArrayList<Invite>();

    /**
     * One-to-many relationship between user and practicalgroup, the practicalgroups where the user is owner of
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<PracticalGroup> practicalGroupsOwner = new ArrayList<PracticalGroup>();

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
     * Method to find the pending invites an user has
     * @return list of pending invites
     * @param practical of the user
     */
    public List<Invite> findPendingInvitesUser(Practical practical) {
        return Invite.findPendingInvitesWhereUser(this, practical);
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
     * Method to add a skill to the list, used for the many-to-many relationship
     * @param skill to add to the list
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    /**
     * Used to return the list of skills
     * @return returns the current list of skills
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * Method to add a practical to the list
     * @param practical to be added to the list
     */
    public void addPractical(Practical practical) {
        practicals.add(practical);
    }

    /**
     * Used to return the list of practicals
     * @return practicals practicals
     */
    public List<Practical> getPracticals() {
        return practicals;
    }

    /**
     * Getter for practicalgroups
     * @return practicalgroups practical groups
     */
    public List<PracticalGroup> getPracticalGroups() {
        return practicalGroups;
    }

    /**
     * Gets identities.
     * @return The identities
     */
    public List<Identity> getIdentities() {
        return identities;
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
     * Getter of the email
     * @return email email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter of the email
     * @param email to be set
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
     * Gets profile text.
     * @return The profile text
     */
    public String getProfileText() {
        return profileText;
    }

    /**
     * Sets profile text.
     * @param profileText The profile text
     */
    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    /**
     * Gets profile image.
     * @return The profile image
     */
    public ProfileImage getProfileImage() {
        return profileImage;
    }

    /**
     * Sets profile image.
     * @param profileImage The profile image
     */
    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Gets profile image or Gravatar url.
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
     * Check if we have a password identity
     * @return True if found else false
     */
    public Boolean hasPassword() {
        // Go trough all identities and see if we have a password identity
        for(Identity identity : identities) {
            if(identity instanceof PasswordIdentity) {
                return true;
            }
        }

        return false;
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

    /**
     * Sends an email with verification link
     */
    public void sendVerification() {
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject("APMatch - Verify your mail");
        mail.setRecipient(this.getFullName() + " <" + this.getEmail() + ">");
        mail.setFrom("APMatch <" + EMAIL + ">");
        //sends text/text
        String link = controllers.routes.Authentication.verify(this.getEmail(), this.getToken()).absoluteURL(false,
                Http.Context.current()._requestHeader());
        String message = "Verify your account by opening this link: " + link;

        // Avoid testing accounts
        if(!this.getEmail().contains("@example.com") && !this.getEmail().contains("@test.com")) {
            mail.send(message);
        }
    }

    /**
     * Getter invites send
     * @return invites send
     */
    public List<Invite> getInvitesSend() {
        return invitesSend;
    }

    /**
     * Getter invites received
     * @return invites received
     */
    public List<Invite> getInvitesReceived() {
        return invitesReceived;
    }
}
