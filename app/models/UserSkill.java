package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

/**
 * Created by Floris on 26/05/14.
 */
@Entity
public class UserSkill extends Model {

    /**
     * The user skill identifier
     */
    @Id
    private Long id;

    /**
     * The value of the skill for this user
     */
    @Constraints.Required
    private Integer value;

    /**
     * The user which the user skill is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected User user;

    /**
     * The skill which the user skill is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected Skill skill;

    /**
     * Constructor for the user skill class.
     * @param user The user for this user skill
     * @param skill The skill for this user skill
     * @param val The value of the skill for this user
     */
    public UserSkill(User user, Skill skill, Integer val) {
        this.user = user;
        this.skill = skill;
        value = val;
    }

    /**
     * Get the user linked to the user skill
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * Link the user to the user skill
     * @param user The user to link
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the skill linked to the user skill
     * @return The skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Link the skill to the user skill
     * @param skill The skill to link
     */
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    /**
     * Get of the value for this user skill
     * @return The value for this user skill
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Set of the value for this user skill
     * @param value The value of the skill for this user
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Define the finder for Ebean
     */
    public static Finder<Long, UserSkill> find = new Finder<Long, UserSkill>(
            Long.class, UserSkill.class
    );

}
