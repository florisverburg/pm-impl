package forms;

import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 13/05/14.
 */
public class RegisterForm {

    /**
     * The registration name
     */
    @Constraints.Required
    public String name;

    /**
     * The registration email address
     */
    @Constraints.Required
    @Constraints.Email
    public String email;

    /**
     * The registration password
     */
    @Constraints.Required
    @Constraints.MinLength(8)
    public String password;

    /**
     * The registration password repeat
     */
    @Constraints.Required
    public String passwordRepeat;

    /**
     * Validates the registration form
     * @return A list of errors
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        // Check if email is already existing in database
        if(User.byEmail(email) != null) {
            errors.add(new ValidationError("email", "This email address is already registered."));
        }

        // Check if passwords match
        if(!password.equals(passwordRepeat)) {
            errors.add(new ValidationError("passwordRepeat", "The password doesn't match."));
        }

        // Check if there are any errors
        if(errors.size() > 0) {
            return errors;
        }
        else {
            return null;
        }
    }

    /**
     * Return a new user based on the registration form
     * @return The new user
     */
    private User getUser() {
        return new User(name, "", "", email);
    }

    /**
     * Returns a new password based identity based on the registration form
     * @param user The user which the identity must be linked to
     * @return The new password identity
     */
    private Identity getIdentity(User user) {
        return new PasswordIdentity(user, email, password);
    }

    /**
     * Generate the user and identity based on the entered form information
     */
    public void save() {
        User user = this.getUser();
        user.save();
        this.getIdentity(user).save();
    }
}
