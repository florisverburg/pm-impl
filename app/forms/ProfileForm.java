package forms;

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
    @Constraints.MaxLength(value = 255, groups = {All.class})
    private String profileText;

    /**
     * The profile image
     */
    @Constraints.Required(groups = {All.class})
    private User.ProfileImage profileImage;

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
    }

    /**
     * Gets profile text that is set in the form
     * @return The profile text
     */
    public String getProfileText() {
        return profileText;
    }

    /**
     * Sets profile text that is shown in the form
     * @param profileText The profile text
     */
    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    /**
     * Gets profile image that is set in the form
     * @return The profile image
     */
    public User.ProfileImage getProfileImage() {
        return profileImage;
    }

    /**
     * Sets profile image that is shown in the form
     * @param profileImage The profile image
     */
    public void setProfileImage(User.ProfileImage profileImage) {
        this.profileImage = profileImage;
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
        if(this.password != null && !this.password.isEmpty() && PasswordIdentity.contains(user)) {
            for(Identity identity : user.getIdentities()) {
                if(identity instanceof PasswordIdentity) {
                    ((PasswordIdentity) identity).setPassword(this.password);
                    identity.save();
                }
            }
        }

        user.save();
    }
}
