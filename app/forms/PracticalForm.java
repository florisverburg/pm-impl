package forms;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Freek on 26/05/14.
 * The basic practical form for the teachers
 */
public class PracticalForm {

    /**
     * Where all the form entries are checked
     */
    public interface All {}

    /**
     * During registration only several fields are required
     */
    public interface Registration {}

    /**
     * The Name.
     */
    @Constraints.Required(groups = {All.class, Registration.class})
    String name;

    /**
     * The Description.
     */
    @Constraints.Required(groups = {All.class, Registration.class})
    String description;

    /**
     * The list with skill forms
     */
    @Constraints.Required(groups = {All.class})
    private List<SkillsForm> skills = new ArrayList<SkillsForm>();

    /**
     * Create a new empty practical form
     */
    public PracticalForm() {

    }

    /**
     * Create a new practical form based on a practical
     * @param practical The practical
     */
    public PracticalForm(Practical practical) {
        this.name = practical.getName();
        this.description = practical.getDescription();

        // Add the skills to the form and check if they are set or not
        Map<Skill, SkillValuePractical> skillsMaps = SkillValuePractical.findAllSkills(practical);
        for(Skill skill : skillsMaps.keySet()) {
            if(skillsMaps.get(skill) == null) {
                SkillValue uSkill = new SkillValuePractical(practical, skill, 1);
                skills.add(new SkillsForm(uSkill));
            }
            else {
                skills.add(new SkillsForm(skillsMaps.get(skill)));
            }
        }
    }

    /**
     * Gets name.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description.
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets skills.
     * @return The skills
     */
    public List<SkillsForm> getSkills() {
        return skills;
    }

    /**
     * Sets skills.
     * @param skills The skills
     */
    public void setSkills(List<SkillsForm> skills) {
        this.skills = skills;
    }

    /**
     * Save the the practical from the form
     * @param user The user which is the admin of the practical
     * @return The created practical
     */
    public Practical save(User user) {
        Practical practical = new Practical(this.name, this.description);
        practical.setAdmin(user);
        practical.addUser(user);
        practical.save();

        return practical;
    }

    /**
     * Save the practical from the form using a previous practical
     * @param practical The practical to edit
     * @return The editted practical
     */
    public Practical save(Practical practical) {
        practical.setName(this.name);
        practical.setDescription(this.description);
        practical.save();

        Ebean.delete(SkillValue.find.where().eq("practical.id", practical.getId()).findList());

        // Save all the skills
        for(SkillsForm sForm : skills) {
            sForm.updatePracticalSkill(practical);
        }

        return practical;
    }
}
