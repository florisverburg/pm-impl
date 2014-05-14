package models;


import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
@Entity
public class Right extends Model {

    /**
     * Id of the right model class
     */
    @Id
    private Long id;

    /**
     * Type of the right
     */
    private String type;

    /**
     * Description of the right
     */
    private String description;

    /**
     * Many-to-many relationship of the right model with the team model
     */
    @ManyToMany(mappedBy = "rights", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList();

    /**
     * Finder defined to be able to look up a right
     */
    private static Model.Finder<Long, Right> find =
            new Model.Finder<Long, Right>(Long.class, Right.class);

    /**
     * Constructor of the right model class
     * @param tp type of the right
     * @param descript description of the right
     */
    public Right(String tp, String descript) {
        type = tp;
        description = descript;
    }

    /**
     * Method to add a list to a team
     * @param team to be added to the list
     */
    public void addTeam(Team team) {
        teams.add(team);
    }

    /**
     * Getter of the type
     * @return the type of the team
     */
    public String getType() {
        return type;
    }

    /**
     * Getter of the teams
     * @return the teams associated with this right
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Method to find a right given by the name
     * @param type of the right that needs to be found
     * @return right with the proper lists added
     */
    public static Right findByName(String type) {
        return find.where().eq("type", type).findUnique();
    }

    /**
     * Getter for the description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter of the type
     * @param type to be set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter of the description
     * @param description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
