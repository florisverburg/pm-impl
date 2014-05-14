package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 12-5-2014.
 */
@Entity
public class Skill extends Model {

    /**
     * Enumerate defined for the different types of skills an user can have
     */
    public enum Type {
        /**
         * Programming skill
         */
        @EnumValue("P")
        PROGRAMMING,
        /**
         * Documenting skill
         */
        @EnumValue("D")
        DOCUMENTING
    }

    /**
     * The unique identifier of a skill
     */
    @Id
    private Long id;

    /**
     *  The name of the skill
     */
    private String name;

    /**
     *  The kind of skill
     */
    private Type type;

    /**
     * The max value a skill can has
     */
    private Integer maxValue;

    /**
     * The many-to-many relationship defined for the skills and users
     */
    @ManyToMany(mappedBy = "skills", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList();

    /**
     * Finder defined for the Skills, used in the many-to-many relationship
     */
    private static Model.Finder<Long, Skill> find =
            new Model.Finder<Long, Skill>(Long.class, Skill.class);

    /**
     * Getter for the Name
     * @return name of the current skill
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of the name
     * @param name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add a user to the list for the many-to-many relationship
     * @param user to add to the list
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Getter for the users
     * @return users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Constructor of the Skill model class
     * @param nm name of the skill
     * @param tp type of the skill
     * @param maxV maxValue of the skill
     */
    public Skill(String nm, Type tp, Integer maxV) {
        name = nm;
        maxValue = maxV;
        type = tp;
    }

    /**
     * Getter of the type
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Setter of the type
     * @param type to be set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Getter of the max value
     * @return maxValue
     */
    public Integer getMaxValue() {
        return maxValue;
    }

    /**
     * Setter of the max value
     * @param maxValue to be set
     */
    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Method to find a skill by its name
     * @param name of the skill to be found
     * @return skill with the name
     */
    public static Skill findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }
}
