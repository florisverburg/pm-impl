package models;

import com.avaje.ebean.Ebean;
import play.data.validation.*;

import javax.persistence.*;
import java.util.List;

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

    /**
     * Find the skills of a specific practical
     * @param practical The practical to search the skills for
     * @return The skill values
     */
    public static List<SkillValuePractical> findByPractical(Practical practical) {
        return Ebean.find(SkillValuePractical.class).where().eq("practical.id", practical.getId()).findList();
    }
}
