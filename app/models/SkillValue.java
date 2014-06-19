package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

/**
 * Created by Floris on 26/05/14.
 * The basic skill with a value attached
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="type")
public abstract class SkillValue extends Model {

    /**
     * The user skill identifier
     */
    @Id
    protected Long id;

    /**
     * The value of the skill for this user
     */
    @Constraints.Required
    protected Integer value;

    /**
     * The skill which the user skill is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected Skill skill;

    /**
     * Gets id.
     * @return The id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the skill linked to the user skill
     * @return The skill
     */
    public Skill getSkill() {
        return this.skill;
    }

    /**
     * Link the skill to the value skill
     * @param skill The skill to link
     */
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    /**
     * Get of the value for this value skill
     * @return The value for this value skill
     */
    public Integer getValue() {
        return this.value;
    }

    /**
     * Set of the value for this value skill
     * @param value The value of the skill
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Define the finder for Ebean
     */
    public static Finder<Long, SkillValue> find = new Finder<Long, SkillValue>(
            Long.class, SkillValue.class
    );

}
