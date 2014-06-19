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
     * @param name The name of the skill
     * @param type The type of the skill
     */
    public Skill(String name, Type type) {
        this.name = name;
        this.type = type;
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
