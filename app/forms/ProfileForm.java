package forms;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 20/05/14.
 * Profile editing form
 */
public class ProfileForm extends RegisterForm {

    /**
     * The profile text
     */
    private String profileText;

    /**
     * The profile image
     */
    @Constraints.Required(groups = {All.class})
    private User.ProfileImage profileImage;

    /**
     * The list with skill forms
     */
    @Constraints.Required(groups = {All.class})
    private List<SkillsForm> profileSkills = new ArrayList<SkillsForm>();

    /**
     * Generate an empty form
     */
    public ProfileForm() {

    }

    /**
     * Create a new Profile form based on a user
     * @param user The user
     */
    public ProfileForm(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.profileText = user.getProfileText();
        this.profileImage = user.getProfileImage();

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
     * Gets profile text.
     * @return The profile text
     */
    public String getProfileText() {
        return profileText;
    }

    /**
     * Sets profile text.
     * @param profileText The profile text
     */
    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    /**
     * Gets profile image.
     * @return The profile image
     */
    public User.ProfileImage getProfileImage() {
        return profileImage;
    }

    /**
     * Sets profile image.
     * @param profileImage The profile image
     */
    public void setProfileImage(User.ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Getter for the profile skills.
     * @return The profile skills
     */
    public List<SkillsForm> getProfileSkills() {
        return profileSkills;
    }

    /**
     * Setter for the profile skills.
     * @param profileSkills The profile skills
     */
    public void setProfileSkills(List<SkillsForm> profileSkills) {
        this.profileSkills = profileSkills;
    }

    /**
     * Validates the profile form
     * @return A list of errors
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        // Check if passwords match
        if(password != null && !password.equals(passwordRepeat)) {
            errors.add(new ValidationError("passwordRepeat", "error.passwordRepeat"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Update a user with the new profile information
     * @param user The user to update
     */
    public void updateUser(User user) {
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setProfileText(this.profileText);
        user.setProfileImage(this.profileImage);

        // Update identity if needed
        if(this.password != null && !this.password.isEmpty() && user.hasPassword()) {
            for(Identity identity : user.getIdentities()) {
                if(identity instanceof PasswordIdentity) {
                    ((PasswordIdentity) identity).setPassword(this.password);
                    identity.save();
                }
            }
        }

        Ebean.delete(SkillValue.find.where().eq("user.id", user.getId()).findList());

        for(SkillsForm sForm : profileSkills) {
            sForm.updateUserSkill(user);
        }

        user.save();
    }
}
