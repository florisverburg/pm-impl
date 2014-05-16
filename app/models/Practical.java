package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 14-5-2014.
 * Model class for the database practical
 */
@Entity
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
     * Many-to-many relationship between practical and user
     */
    @ManyToMany(mappedBy = "practicals", cascade = CascadeType.PERSIST)
    List<User> users = new ArrayList<>();

    /**
     * One-to-many relationship between practical and user
     */
    @ManyToOne
    @JoinColumn(name = "adminId")
    private User admin;

    /**
     * One-to-many relationship between practical and practical group
     */
    @OneToMany(mappedBy = "practical", cascade = CascadeType.ALL)
    List<PracticalGroup> practicalGroups = new ArrayList<>();

    /**
     * Finder defined for the practical
     */
    public static Model.Finder<Long, Practical> find =
            new Model.Finder<>(Long.class, Practical.class);

    /**
     * Constructor of the practical
     * @param name of the practical
     * @param description of the practical
     * @param secret of the practical
     */
    public Practical(String name, String description, String secret) {
        this.name = name;
        this.description = description;
        this.secret = secret;
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
     * Method to find a practical by its id
     * @param id of the practical to be found
     * @return the found practical
     */
    public static Practical findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * Getter for id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for id
     * @param id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for name
     * @return name
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
     * @return description
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
     * @return admin
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
     * @return secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Setter for the secret
     * @param secret to set
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Getter for users
     * @return users
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
    public void addUsers(User user) {
        users.add(user);
    }

    /**
     * Getter for practicalGroups
     * @return practicalGroups
     */
    public List<PracticalGroup> getPracticalGroups() {
        return practicalGroups;
    }

    /**
     * Setter for practicalGroups
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
}
