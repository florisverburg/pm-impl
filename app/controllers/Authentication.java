package controllers;

import forms.LoginForm;
import forms.RegisterForm;
import play.mvc.*;
import play.data.*;
import views.html.*;
import static play.data.Form.*;

/**
 * Created by Freek on 12/05/14.
 * This class handles all the authentication methods like login and register.
 */
public class Authentication extends SecuredController {

    /**
     * Shows the login page
     * @return The login page
     */
    public static Result login() {
        return ok(
                login.render(form(LoginForm.class))
        );
    }

    /**
     * Shows the register page
     * @return The register page
     */
    public static Result register() {
        return ok(
                register.render(form(RegisterForm.class))
        );
    }

    /**
     * Authenticates the user
     * @return Error when authentication fails or redirect if successful
     */
    public static Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();

        // Check for errors in login
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        }
        else {
            // Set the session and redirect to home
            session().clear();
            session("user_id", loginForm.get().getUser().getId().toString());
            flash("success", "authentication.loggedIn");
            return redirect(
                    routes.Application.index()
            );
        }
    }

    /**
     * Register a new user
     * @return Error when registration isn't complete else a redirect
     */
    public static Result registration() {
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();

        // Check for errors in register
        if(registerForm.hasErrors()) {
            return badRequest(register.render(registerForm));
        }
        else {
            // Create the new user and identity
            registerForm.get().save();

            return redirect(
                    routes.Application.index()
            );
        }
    }

    /**
     * Logs out the current user
     * @return Redirects to the application home page
     */
    @Security.Authenticated(Secured.class)
    public static Result logout() {
        flash("success", "authentication.loggedOut");
        session().clear();
        return redirect(
                routes.Application.index()
        );
    }
}
