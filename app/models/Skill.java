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
public class Skill {

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
     * Add a user to the list for the many-to-many relationship
     * @param user to add to the list
     */
    public void addUser(User user) {
        users.add(user);
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
}
