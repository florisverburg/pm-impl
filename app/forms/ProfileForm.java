package forms;

import models.*;
import play.data.validation.*;

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
     * Whether the user had a password login
     */
    private boolean hasPassword;

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
        this.hasPassword = false;

        // Go trough all identities and see if we have a password identity
        List<Identity> identities = user.getIdentities();
        for(Identity identity : identities) {
            if(identity instanceof PasswordIdentity) {
                this.hasPassword = true;
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
     * If the user has a password login
     * @return Whether the user has a password login
     */
    public boolean hasPassword() {
        return hasPassword;
    }

    /**
     * Sets if the user has a password login
     * @param hasPassword Whether the user has a password login
     */
    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    /**
     * Update a user with the new profile information
     * @param user The user to update
     * @return The updated user
     */
    public User updateUser(User user) {
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        return user;
    }
}
