package models;

import play.data.validation.*;

import javax.persistence.*;

/**
 * Created by Freek on 02/06/14.
 * Basic Practical valued skill
 */
@Entity
@DiscriminatorValue("Practical")
public class SkillValuePractical extends SkillValue {

    /**
     * The practical which the skill is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected Practical practical;

    /**
     * Create a new skill value based on a practical
     * @param practical The practical it is linked to
     * @param skill The skill that is valued
     * @param value The value of the skill
     */
    public SkillValuePractical(Practical practical, Skill skill, Integer value) {
        this.practical = practical;
        this.skill = skill;
        this.value = value;
    }

    /**
     * Gets practical.
     * @return The practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Sets practical.
     * @param practical The practical
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }
}
