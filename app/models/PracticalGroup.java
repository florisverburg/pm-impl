package models;

import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 * Class for the model representation of the database table practicalgroup
 */
@Entity
@SuppressWarnings("serial")
public class PracticalGroup extends Model {

    /**
     * primary key of the practicalgroup
     */
    @Id
    private long id;

    /**
     * Many-to-one relationship of the practicalgroup and the practical
     */
    @ManyToOne
    @JoinColumn(name = "practicalId")
    private Practical practical;

    /**
     * Many-to-many relationship of the practicalgroups and users
     */
    @ManyToMany(mappedBy = "practicalGroups", cascade = CascadeType.ALL)
    List<User> users = new ArrayList<User>();

    /**
     * Finder defined for the practicalgroup
     */
    public static Model.Finder<Long, PracticalGroup> find =
            new Model.Finder<Long, PracticalGroup>(Long.class, PracticalGroup.class);

    /**
     * Constructor of practical group
     * @param practical to which this group belongs to
     */
    public PracticalGroup(Practical practical) {
        this.practical = practical;
    }

    /**
     * function to find a practical group by it's id
     * @param id of the practical group to be found
     * @return practical group that was found
     */
    public static PracticalGroup findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * A method to find a practical group using a given practical and a given user
     * @param practical of the practical group
     * @param user of the practical group
     * @return practical group that was sought after
     */
    public static PracticalGroup findWithPracticalAndUser(Practical practical, User user) {
        return find.where()
                .eq("practicalId", practical.getId())
                .eq("users.id", user.getId())
                .findUnique();
    }

    /**
     * Getter for users
     * @return users
     */
    public List<User> getUsers() {
        return users;
    }

    /** Setter for users
     * @param users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Add user to list
     * @param user to add
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Remove user from list
     * @param user to remove
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Getter for Id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for Id
     * @param id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for practical
     * @return practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Setter for practical
     * @param practical to set
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }
}
