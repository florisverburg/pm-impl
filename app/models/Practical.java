package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 14-5-2014.
 * Model class for the database practical
 */
@Entity
@SuppressWarnings("serial")
public class Practical extends Model {

    /**
     * Primary key of the practical
     */
    @Id
    private long id;

    /**
     * Name of the practical
     */
    @Constraints.Required
    @Constraints.MaxLength(50)
    private String name;

    /**
     * Description of the practical
     */
    @Constraints.Required
    @Constraints.MaxLength(200)
    private String description;


    /**
     * Secret used to register an user to a practical
     */
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String secret;

    /**
     * The amount of random bits that needs to be generated for the secret
     * */
    private static final int SECRET_RANDOM_BITS = 130;

    /**
     * The base number of the random secret generated number
     */
    private static final int SECRET_RANDOM_BASE = 16;

    /**
     * Many-to-many relationship between practical and user
     */
    @ManyToMany(cascade = CascadeType.ALL)
    List<User> users = new ArrayList<User>();

    /**
     * One-to-many relationship between practical and user
     */
    @ManyToOne
    private User admin;

    /**
     * Finder defined for the practical
     */
    public static Model.Finder<Long, Practical> find =
            new Finder<Long, Practical>(Long.class, Practical.class);

    /**
     * Constructor of the practical
     * @param name of the practical
     * @param description of the practical
     */
    public Practical(String name, String description) {
        this.name = name;
        this.description = description;
        this.secret = generateSecret();
    }

    /**
     * Method to find a practical by its id
     * @param id of the practical to be found
     * @return the found practical
     */
    public static Practical findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * Method to find a practical by its name
     * @param name of the practical to be found
     * @return the found practical
     */
    public static Practical findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    /**
     * Find the practicals a user is registered to ordered by name
     * @param user The user that is registered to the practicals
     * @return The ordered list of practicals
     */
    public static List<Practical> findByUser(User user) {
        return find.where().eq("users.id", user.getId()).orderBy("name").findList();
    }

    /**
     * Getter for id
     * @return id id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for name
     * @return name name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for description
     * @return description description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for admin
     * @return admin admin
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * Setter for admin
     * @param admin to set
     */
    public void setAdmin(User admin) {
        this.admin = admin;
    }

    /**
     * Getter for the secret
     * @return secret secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Getter for users
     * @return users users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Setter for users
     * @param users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Method to add a user to the list
     * @param user to add
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Setter secret
     * @param secret to set
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Method that returns a random generated secret
     * @return random generated secret
     */
    public String generateSecret() {
        return new BigInteger(SECRET_RANDOM_BITS, new SecureRandom()).toString(SECRET_RANDOM_BASE);
    }

    /**
     * Check if a user is an administrator of the course
     * @param user The user to check
     * @return If the user is the administrator
     */
    public boolean isAdmin(User user) {
        return user.equals(this.admin);
    }

    /**
     * Check if a user is enrolled for a practical
     * @param user The user to check
     * @return Whether the user is enrolled or not
     */
    public boolean isEnrolled(User user) {
        return users.contains(user);
    }
}
