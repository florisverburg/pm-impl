package models;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.*;

import java.util.ArrayList;
import java.util.List;

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
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String firstName;

    /**
     * The last name of an user
     */
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String lastName;

    /**
     * The language of an user
     */
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String language;

    /**
     * The email address of an user
     */
    @Constraints.Required
    @Constraints.Email
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
     * The many-to-many relationship defined by the skills and users
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList();

    /**
     * The many-to-many relationship defined for the users and teams
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList();

    /**
     * Finder to be defined to use the many-to-many relationship of user and skill
     */
    public static Model.Finder<Long, User> find =
            new Model.Finder<Long, User>(Long.class, User.class);

    /**
     * Constructor for the User class
     * @param fName firstName of the user
     * @param lName lastName of the user
     * @param lang language of the user
     * @param eml email of the user
     */
    public User(String fName, String lName, String lang, String eml) {
        firstName = fName;
        lastName = lName;
        language = lang;
        email = eml;
    }

    /**
     * Method to add a skill to the list, used for the many-to-many relationship
     * @param skill skill to add to the list
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    /**
     * Method to add a team to the list
     * @param team to be added to the list
     */
    public void addTeam(Team team) {
        teams.add(team);
    }

    /**
     * Used to return the list of skills
     * @return returns the current list of skills
     */
    public List getSkills() {
        return skills;
    }

    /**
     * Used to return the list of teams this user has
     * @return list of teams
     */
    public List getTeams() {
        return teams;
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
     * @return firstName
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
     * @return lastName
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
     * Getter of the language
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Setter of the language
     * @param language to be set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Getter of the email
     * @return email
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
}
