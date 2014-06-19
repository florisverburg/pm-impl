package models;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import java.util.List;

/**
 * Created by Marijn Goedegebure on 12-5-2014.
 * Right model class for the database model
 */
@Entity
@SuppressWarnings("serial")
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
     *  The name of the skill
     */
    @Id
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String name;

    /**
     *  The kind of skill
     */
    @Constraints.Required
    private Type type;

    /**
     * The max value a skill can has
     */
    @Constraints.Required
    private Integer maxValue;

    /**
     * Finder defined for the Skills, used in the many-to-many relationship
     */
    public static Model.Finder<String, Skill> find =
            new Model.Finder<String, Skill>(String.class, Skill.class);

    /**
     * Getter for the Name
     * @return name of the current skill
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter of the name
     * @param name to be set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Constructor of the Skill model class
     * @param nm name of the skill
     * @param tp type of the skill
     * @param maxV maxValue of the skill
     */
    public Skill(String nm, Type tp, Integer maxV) {
        this.name = nm;
        this.maxValue = maxV;
        this.type = tp;
    }

    /**
     * Getter of the type
     * @return type
     */
    public Type getType() {
        return this.type;
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
        return this.maxValue;
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
        return find.byId(name);
    }

    /**
     * Method to get all the skills
     * @return all the skills
     */
    public static List<Skill> findAll() {
        return find.all();
    }
}
