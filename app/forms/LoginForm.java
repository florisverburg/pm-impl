package forms;

import models.*;
import play.data.validation.*;

/**
 * Created by Freek on 13/05/14.
 * The login form
 */
public class LoginForm {

    /**
     * The user email address
     */
    @Constraints.Required
    @Constraints.Email
    private String email;

    /**
     * The user password
     */
    @Constraints.Required
    private String password;

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
    public String validate() {
        if (User.authenticate(email, password) == null) {
            return "Invalid user or password";
        }
        return null;
    }

    /**
     * Returns the user if the login was valid
     * @return The user
     */
    public User getUser() {
        return User.findByEmail(email);
    }
}
