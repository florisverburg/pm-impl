package models;

import javax.persistence.*;
import play.db.ebean.*;

import java.util.List;
import java.util.ArrayList;


/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
@Entity
public class Team extends Model {

    /**
     * The id of the table
     */
    @Id
    private Long id;

    /**
     * The type of the Team which can have rights
     */
    private String type;

    /**
     * The description of the Team
     */
    private String description;

    /**
     * The many-to-many relationship with the user table
     */
    @ManyToMany(mappedBy = "teams", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList();

    /**
     * THe many-to-many relationship with the right table
     */
    @ManyToMany(cascade =  CascadeType.ALL)
    private List<Right> rights = new ArrayList();

    /**
     * Finder defined for looking up the different teams
     */
    private static Model.Finder<Long, Team> find =
            new Model.Finder<Long, Team>(Long.class, Team.class);

    /**
     * Constructor of the Team model class
     * @param tp type of the Team
     * @param descript description of the Team
     */
    public Team(String tp, String descript) {
        type = tp;
        description = descript;
    }

    /**
     * Getter of the rights
     * @return the rights of the team
     */
    public List<Right> getRights() {
        return rights;
    }

    /**
     * Getter of the users
     * @return the users of the team
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Add a user to the list of users of this team
     * @param user that needs to be added.
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Add a right to the list of rights of this team
     * @param right that needs to be added.
     */
    public void addRight(Right right) {
        rights.add(right);
    }

    /**
     * Method to find a team by its type name
     * @param type of the team to be found
     * @return a team that has a type equal to the given type
     */
    public static Team findByName(String type) {
        return find.where().eq("type", type).findUnique();
    }

    /**
     * Getter of the type
     * @return the type
     */
    public String getType() {
        return type;
    }
}
