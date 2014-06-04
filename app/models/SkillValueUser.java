package models;

import play.data.validation.*;
import javax.persistence.*;

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
        return user;
    }

    /**
     * Link the user to the user skill
     * @param user The user to link
     */
    public void setUser(User user) {
        this.user = user;
    }
}
