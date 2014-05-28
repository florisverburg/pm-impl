package forms;

import models.*;

/**
 * Created by Floris on 26/05/14.
 * The skills form.
 */
public class SkillsForm {

    /**
     * The skills name.
     */
    private String name;

    /**
     * The user skills value
     */
    private Integer value;

    /**
     * Empty constructor for the skills form.
     */
    public SkillsForm() {

    }

    /**
     * Constructor for the skills form.
     * @param uSkill The user skill to fill the form
     */
    public SkillsForm(UserSkill uSkill) {
        this.name = uSkill.getSkill().getName();
        this.value = uSkill.getValue();
    }

    /**
     * Gets the name.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets value.
     * @return The value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value The value
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Method to update the user skill.
     * @param user The user from who the user skill is
     */
    public void updateUserSkill(User user) {
        UserSkill userSkill = new UserSkill(user, Skill.findByName(this.name), this.value);
        userSkill.save();
    }
}
