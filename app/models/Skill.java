package models;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import java.util.ArrayList;
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
     * One-to-many relationship between user skill and skill
     */
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private List<UserSkill> userSkills = new ArrayList<UserSkill>();

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
     * Add a user skill to the list for the many-to-many relationship
     * @param userSkill to add to the list
     */
    public void addUserSkill(UserSkill userSkill) {
        userSkills.add(userSkill);
    }

    /**
     * Getter for the user skills
     * @return user skills
     */
    public List<UserSkill> getUserSkills() {
        return userSkills;
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
        return find.byId(name);
    }

    /**
     * Method to get all the skills
     * @return all the skills
     */
    public static List<Skill> findAll() {
        return find.all();
    }

//    /**
//     * Method to find a skill by its id
//     * @param id of the skill to be found
//     * @return skill with the id
//     */
//    public static Skill findById(long id) {
//        return find.where().eq("id", id).findUnique();
//    }
}
