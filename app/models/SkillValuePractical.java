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
