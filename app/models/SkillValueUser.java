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
 * Basic user valued skill
 */
@Entity
@DiscriminatorValue("User")
public class SkillValueUser extends SkillValue {

    /**
     * The user which the user skill is linked to
     */
    @ManyToOne
    @Constraints.Required
    protected User user;

    /**
     * Create a new Skill with a value and a user
     * @param user The user which is linked to the skill
     * @param skill The skill that is valued
     * @param value The value of the skill
     */
    public SkillValueUser(User user, Skill skill, Integer value) {
        this.user = user;
        this.skill = skill;
        this.value = value;
    }

    /**
     * Get the user linked to the user skill
     * @return The user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Link the user to the user skill
     * @param user The user to link
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Find the skills of a specific user
     * @param user The user to search the skills for
     * @return The skill values
     */
    public static List<SkillValueUser> findByUser(User user) {
        return Ebean.find(SkillValueUser.class).where().eq("user.id", user.getId()).findList();
    }

    /**
     * Find the skill of a specific user
     * @param user The user to search the skills for
     * @param skill The skill to search for
     * @return The skill values
     */
    public static SkillValueUser findByUserSkill(User user, Skill skill) {
        return Ebean.find(SkillValueUser.class).where()
                .and(
                        Expr.eq("user.id", user.getId()),
                        Expr.eq("skill.name", skill.getName())
                ).findUnique();
    }

    /**
     * Find all the skills, including the skills the user hasn't an user skill for.
     * @param user The user the skills are from
     * @return A map with all the skills and skill values
     */
    public static Map<Skill, SkillValueUser> findAllSkills(User user) {
        Map<Skill, SkillValueUser> result = new HashMap<Skill, SkillValueUser>();

        // Go trough all the skills and add them to the map
        for(Skill skill : Skill.findAll()) {
            result.put(skill, findByUserSkill(user, skill));
        }

        return result;
    }
}
