package forms;

import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 13/05/14.
 * The registration form
 */
public class RegisterForm {

    /**
     * The registration first name
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String firstName;

    /**
     * The registration last name
     */
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String lastName;

    /**
     * The registration email address
     */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * The registration password
     */
    @Constraints.Required
    @Constraints.MinLength(8)
    private String password;

    /**
     * The registration password repeat
     */
    @Constraints.Required
    private String passwordRepeat;

    /**
     * Gets the first name.
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
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
     * Validates the registration form
     * @return A list of errors
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        // Check if email is already existing in database
        if(User.findByEmail(email) != null) {
            errors.add(new ValidationError("email", "This email address is already registered."));
        }

        // Check if passwords match
        if(!password.equals(passwordRepeat)) {
            errors.add(new ValidationError("passwordRepeat", "error.passwordRepeat"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Return a new user based on the registration form
     * @return The new user
     */
    private User getUser() {
        return new User(firstName, lastName, email);
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
