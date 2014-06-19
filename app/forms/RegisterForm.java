package forms;

import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 13/05/14.
 * The registration form
 */
public class RegisterForm extends LoginForm {

    /**
     * The registration first name
     */
    @Constraints.Required(groups = {All.class, Login.class})
    @Constraints.MaxLength(value = 128, groups = {All.class, Login.class})
    protected String firstName;

    /**
     * The registration last name
     */
    @Constraints.Required(groups = {All.class, Login.class})
    @Constraints.MaxLength(value = 128, groups = {All.class, Login.class})
    protected String lastName;

    /**
     * The registration password repeat
     */
    @Constraints.Required(groups = {Login.class})
    protected String passwordRepeat;

    /**
     * Gets the first name.
     * @return The first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the first name.
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
        return this.lastName;
    }

    /**
     * Sets last name.
     * @param lastName The last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets password repeat.
     * @return The password repeat
     */
    public String getPasswordRepeat() {
        return this.passwordRepeat;
    }

    /**
     * Sets password repeat.
     * @param passwordRepeat The password repeat
     */
    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    /**
     * Validates the registration form
     * @return A list of errors
     */
    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        // Check if email is already existing in database
        if(User.findByEmail(this.email) != null) {
            errors.add(new ValidationError("email", "error.doubleEmail"));
        }

        // Check if passwords match
        if(!this.password.equals(this.passwordRepeat)) {
            errors.add(new ValidationError("passwordRepeat", "error.passwordRepeat"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Return a new user based on the registration form
     * @return The new user
     */
    private User getNewUser() {
        return new User(this.firstName, this.lastName, this.email);
    }

    /**
     * Returns a new password based identity based on the registration form
     * @param user The user which the identity must be linked to
     * @return The new password identity
     */
    private Identity getIdentity(User user) {
        return new PasswordIdentity(user, this.email, this.password);
    }

    /**
     * Generate the user and identity based on the entered form information
     * @return The newly created user
     */
    public User save() {
        User user = this.getNewUser();
        user.save();
        this.getIdentity(user).save();

        return user;
    }
}
