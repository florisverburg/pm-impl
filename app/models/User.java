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
     * The email address of an user
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

    /**
     * The many-to-many relationship defined by the skills and users
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList();

    /**
     * Finder to be defined to use the many-to-many relationship of user and skill
     */
    private static Model.Finder<Long, User> find =
            new Model.Finder<Long, User>(Long.class, User.class);

    /**
     * Constructor for the User class
     * @param fName firstName of the user
     * @param lName lastName of the user
     * @param lang language of the user
     * @param loginNm loginName of the user
     * @param eml email of the user
     */
    public User(String fName, String lName, String lang, String loginNm, String eml)
    {
        firstName = fName;
        lastName = lName;
        language = lang;
        loginName = loginNm;
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
     * @param name name that will be used to search for
     * @return returns the User that has a name equal to the input
     */
    public static User findByName(String name) {
        return find.where().eq("firstName", name).findUnique();
    }
}
