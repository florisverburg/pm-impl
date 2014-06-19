package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.data.validation.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return this.practical;
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

    /**
     * Find the skill of a specific practical
     * @param practical The practical to search the skills for
     * @param skill The skill to search for
     * @return The skill values
     */
    public static SkillValuePractical findByUserSkill(Practical practical, Skill skill) {
        return Ebean.find(SkillValuePractical.class).where()
                .and(
                        Expr.eq("practical.id", practical.getId()),
                        Expr.eq("skill.name", skill.getName())
                ).findUnique();
    }

    /**
     * Find all the skills, including the skills the practical hasn't an practical skill for.
     * @param practical The practical to search the skills for
     * @return A map with all the skills and skill values
     */
    public static Map<Skill, SkillValuePractical> findAllSkills(Practical practical) {
        Map<Skill, SkillValuePractical> result = new HashMap<Skill, SkillValuePractical>();

        // Go trough all the skills and add them to the map
        for(Skill skill : Skill.findAll()) {
            result.put(skill, findByUserSkill(practical, skill));
        }

        return result;
    }
}
