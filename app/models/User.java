package models;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import play.data.validation.*;
import play.db.ebean.*;

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
     *
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Identity> identities = new ArrayList<Identity>();

    /**
     * The many-to-many relationship defined by the skills and users
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<Skill>();

    /**
     * The many-to-many relationship defined for the users and practicals
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
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
     * @param skill skill to add to the list
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    /**
     * Method to add a practical to the list
     * @param practical to be added to the list
     */
    public void addPractical(Practical practical) {
        practicals.add(practical);
    }

    /**
     * Used to return the list of skills
     * @return returns the current list of skills
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * Used to return the list of practicals
     * @return practicals practicals
     */
    public List<Practical> getPracticals() {
        return practicals;
    }

    /**
     * Used to return the list of practicals the user is admin of
     * @return list of practical the user is admin of
     */
    public List<Practical> getPracticalsAdmin() {
        return practicalsAdmin;
    }

    /**
     * Getter for practicalgroups
     * @return practicalgroups practical groups
     */
    public List<PracticalGroup> getPracticalGroups() {
        return practicalGroups;
    }

    /**
     * Setter for practicalgroups
     * @param practicalGroups to set
     */
    public void setPracticalGroups(List<PracticalGroup> practicalGroups) {
        this.practicalGroups = practicalGroups;
    }

    /**
     * Add a practicalgroup to the list
     * @param practicalGroup to add
     */
    public void addPracticalGroup(PracticalGroup practicalGroup) {
        this.practicalGroups.add(practicalGroup);
    }

    /**
     * Setter of the list of practicals the user is admin of
     * @param practicalsAdmin the user is admin of
     */
    public void setPracticalsAdmin(List<Practical> practicalsAdmin) {
        this.practicalsAdmin = practicalsAdmin;
    }

    /**
     * Add a practical to the list of practicals the user is admin of
     * @param practical to be added to the list
     */
    public void addPracticalAdmin(Practical practical) {
        practicalsAdmin.add(practical);
    }

    /**
     * Gets identities.
     * @return The identities
     */
    public List<Identity> getIdentities() {
        return identities;
    }

    /**
     * Sets identities.
     * @param identities The identities
     */
    public void setIdentities(List<Identity> identities) {
        this.identities = identities;
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
}
