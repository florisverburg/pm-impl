package forms;

import com.avaje.ebean.Ebean;
import helpers.Secure;
import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 20/05/14.
 * Profile editing form
 */
public class ProfileForm {

    /**
     * The profile first name
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String firstName;

    /**
     * The profile last name
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String lastName;

    /**
     * The profile email address
     */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * The profile password
     */
    @Constraints.MinLength(8)
    private String password;

    /**
     * The registration password repeat
     */
    private String passwordRepeat;

    /**
     * The profile text
     */
    private String profileText;

    /**
     * The profile image
     */
    @Constraints.Required
    private User.ProfileImage profileImage;

    @Constraints.Required
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
        for(Skill skill : Secure.getUser().findAllSkills()) {
            List<UserSkill> uSkills = skill.getUserSkills();
            if(uSkills.isEmpty()) {
                UserSkill uSkill = new UserSkill(Secure.getUser(), skill, 1);
                profileSkills.add(new SkillsForm(uSkill));
            }
            else {
                profileSkills.add(new SkillsForm(uSkills.get(0)));
            }
        }
    }

    /**
     * Gets first name.
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     * @param firstName The first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     * @param lastName The last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets email.
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets password.
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets password repeat.
     * @return The password repeat
     */
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    /**
     * Sets password repeat.
     * @param passwordRepeat The password repeat
     */
    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
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

        Ebean.delete(UserSkill.find.where().eq("user.id", user.getId()).findList());

        for(SkillsForm sForm : profileSkills) {
            sForm.updateUserSkill(Secure.getUser());
        }

        user.save();
    }
}
