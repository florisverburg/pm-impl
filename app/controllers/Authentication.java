package controllers;

import models.*;
import play.mvc.*;
import play.data.*;
import views.html.*;
import static play.data.Form.*;

/**
 * Created by Freek on 12/05/14.
 * This class handles all the authentication methods like login and register.
 */
public class Authentication extends Controller {

    /**
     * The login form
     */
    public static class Login {

        /**
         * The user email address
         */
        private String email;

        /**
         * The user password
         */
        private String password;

        /**
         * Validates the form
         * @return returns null if login is correct, else an error message
         */
        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    /**
     * Shows the login page
     * @return The login page
     */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    /**
     * Authenticates the user
     * @return Error when authentication fails or redirect if successful
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        }
        else {
            session().clear();
            session("user_id", loginForm.get().email);
            return redirect(
                    routes.Application.index()
            );
        }
    }
}
