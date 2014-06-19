package models;

import javax.persistence.*;

import com.avaje.ebean.Expr;
import com.avaje.ebean.annotation.EnumValue;
import helpers.MD5;
import play.data.validation.*;
import play.db.ebean.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


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
     * The identities linked to the user
     * One-to-many relationship between identity and user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Identity> identities = new ArrayList<Identity>();

    /**
     * One-to-many relationship between user skill and user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SkillValueUser> skillValues = new ArrayList<SkillValueUser>();

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
    @ManyToMany(targetEntity = PracticalGroup.class, cascade = CascadeType.ALL)
    private List<PracticalGroup> practicalGroups = new ArrayList<PracticalGroup>();

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
     * Method to add a user skill to the list, used for the one-to-many relationship
     * @param skillValue user skill to add to the list
     */
    public void addUserSkill(SkillValueUser skillValue) {
        skillValues.add(skillValue);
    }

    /**
     * Used to return the list of skills
     * @return returns the current list of skills
     */
    public List<SkillValueUser> getSkillValues() {
        return skillValues;
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

    /**
     * Gets the invites send
     * @return Invites send
     */
    public List<Invite> getInvitesSend() {
        return invitesSend;
    }

    /**
     * Gets invites send for a certain practical
     * @param practical The practical
     * @return Invites send
     */
    public List<Invite> getInvitesSend(Practical practical) {
        return Invite.find.where().and(
                Expr.eq("practical.id", practical.getId()), Expr.eq("sender.id", this.id)
        ).findList();
    }

    /**
     * Gets the invites received
     * @return Invites received
     */
    public List<Invite> getInvitesReceived() {
        return invitesReceived;
    }

    /**
     * Gets invites received for a certain practical
     * @param practical The practical
     * @return Invites received
     */
    public List<Invite> getInvitesReceived(Practical practical) {
        return Invite.find.where().and(
                Expr.eq("practical.id", practical.getId()), Expr.eq("receiver.id", this.id)
        ).findList();
    }

    /**
     * Find the user skill form this user by a skill.
     * @param skill The skill to search the user skill
     * @return The user skill
     */
    public SkillValueUser findUserSkillBySkill(Skill skill) {
        for(SkillValueUser uSkill : this.getSkillValues()) {
            if(uSkill.getSkill().equals(skill)) {
                return uSkill;
            }
        }
        return null;
    }

    /**
     * Find all the skills, including the skills the user hasn't an user skill for.
     * @return A list with all the skills
     */
    public List<Skill> findAllSkills() {
        return Skill.find.fetch("skillValues", "*").where().filterMany("skillValues")
                .eq("user.id", this.id).findList();
    }
}
