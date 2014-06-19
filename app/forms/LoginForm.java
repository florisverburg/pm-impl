package forms;

import models.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Freek on 13/05/14.
 * The login form
 */
public class LoginForm {

    /**
     * The interface for all the constraints
     */
    public interface All {}

    /**
     * The interface for all the login constraints
     */
    public interface Login {}

    /**
     * The user email address
     */
    @Constraints.Required(groups = {All.class, Login.class})
    @Constraints.Email(groups = {All.class, Login.class})
    protected String email;

    /**
     * The user password
     */
    @Constraints.Required(groups = {Login.class})
    @Constraints.MinLength(value = 8, groups = {All.class, Login.class})
    protected String password;

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
     * Validates the form
     * @return Returns null if login is correct, else an error message
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Identity identity = PasswordIdentity.authenticate(email, password);
        User user = null;

        // When the identity exists get the user
        if(identity != null) {
            user = identity.getUser();
        }

        // Check if the user exists and is valid
        if (user == null) {
            errors.add(new ValidationError("email", "error.wrongAuthentication"));
        }
        else if (user.getToken() != null) {
            errors.add(new ValidationError("email", "error.notValidated"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Returns the user if the login was valid
     * @return The user
     */
    public User getUser() {
        return User.findByEmail(email);
    }
}
