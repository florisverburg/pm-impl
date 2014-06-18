package forms;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

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
     * The list with skill forms
     */
    @Constraints.Required(groups = {LoginForm.All.class})
    private List<SkillsForm> profileSkills = new ArrayList<SkillsForm>();

    /**
     * Empty constructor for the skills form.
     */
    public SkillsForm() {

    }

    /**
     * Constructor for the skills form.
     * @param uSkill The user skill to fill the form
     */
    public SkillsForm(SkillValue uSkill) {
        this.name = uSkill.getSkill().getName();
        this.value = uSkill.getValue();
    }

    /**
     * Constructor for the skills form.
     * @param user The user to fill the form
     */
    public SkillsForm(User user) {
        // Add the skills to the form and check if already set
        for(Skill skill : user.findAllSkills()) {
            List<SkillValue> uSkills = skill.getSkillValues();
            if(uSkills.isEmpty()) {
                SkillValue uSkill = new SkillValueUser(user, skill, 1);
                profileSkills.add(new SkillsForm(uSkill));
            }
            else {
                profileSkills.add(new SkillsForm(uSkills.get(0)));
            }
        }
    }

    /**
     * Gets the profile skills from the form
     * @return The profile skills
     */
    public List<SkillsForm> getProfileSkills() {
        return profileSkills;
    }

    /**
     * Sets the profile skills that are shown in the form
     * @param profileSkills The profile skills
     */
    public void setProfileSkills(List<SkillsForm> profileSkills) {
        this.profileSkills = profileSkills;
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
        SkillValueUser skillValue = new SkillValueUser(user, Skill.findByName(this.name), this.value);
        skillValue.save();
    }

    /**
     * Method to update the practical skill.
     * @param practical The practical from which the skill is
     */
    public void updatePracticalSkill(Practical practical) {
        SkillValuePractical skillValue = new SkillValuePractical(practical, Skill.findByName(this.name), this.value);
        skillValue.save();
    }

    /**
     * Update the user skills with the new profile information
     * @param user The user to update
     */
    public void updateSkills(User user) {
        Ebean.delete(SkillValue.find.where().eq("user.id", user.getId()).findList());

        for(SkillsForm sForm : profileSkills) {
            sForm.updateUserSkill(user);
        }

        user.save();
    }
}
