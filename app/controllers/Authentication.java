package controllers;

import forms.LoginForm;
import forms.RegisterForm;
import helpers.Linkedin;
import models.*;
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
     * Generates a new Linkedin URL with a new state saved in the session
     * @return The linkedin URL
     */
    private static String generateLinkedinUri() {
        String state = Linkedin.generateState();
        session().put("linkedin_state", state);

        return Linkedin.generateRedirectUri(state);
    }

    /**
     * Shows the login page
     * @return The login page
     */
    public static Result login() {
        return ok(
                login.render(generateLinkedinUri(), form(LoginForm.class), form(RegisterForm.class))
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
            return badRequest(login.render(generateLinkedinUri(), loginForm, form(RegisterForm.class)));
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
     * Authenticate using the linkedin callback
     * @param code The linkedin code if received else empty string
     * @param state The linkedin state from the session
     * @param error The linkedin error
     * @param errorDescription The linkedin error description
     * @return The authenticated page
     */
    public static Result auth(String code, String state, String error, String errorDescription) {
        String sessionState = session().get("linkedin_state");

        // First check the state and errors
        if(!state.equals(sessionState) || !error.isEmpty() || code.isEmpty()) {
            flash("error", "linkedin.unknownError");
            return redirect(
                    routes.Application.index()
            );
        }

        // Check code is valid
        Linkedin linkedinConnection = Linkedin.fromAccesToken(code);
        if(linkedinConnection == null) {
            flash("error", "linkedin.expiredError");
            return redirect(
                    routes.Application.index()
            );
        }

        // Save the current linkedin Connection and get the Linkedin userId
        linkedinConnection.toSession();
        String linkedinPersonId = linkedinConnection.getPersonId();

        // Check if already exists and else create
        LinkedinIdentity identity = LinkedinIdentity.byPersonId(linkedinPersonId);
        if(identity == null) {
            identity = linkedinConnection.create();
        }

        // Save login to session
        session().clear();
        session("user_id", identity.getUser().getId().toString());
        flash("success", "authentication.loggedIn");
        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Register a new user
     * @return Error when registration isn't complete else a redirect
     */
    public static Result registration() {
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();

        // Check for errors in register
        if(registerForm.hasErrors()) {
            return badRequest(login.render(generateLinkedinUri(), form(LoginForm.class), registerForm));
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
