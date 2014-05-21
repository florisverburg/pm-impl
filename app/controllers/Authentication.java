package controllers;

import forms.LoginForm;
import forms.RegisterForm;
import helpers.LinkedinConnection;
import helpers.Secure;
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
     * Generates a new Linkedin URL with a new state saved in the session
     * @return The linkedin URL
     */
    private static String generateLinkedinUri() {
        String state = LinkedinConnection.generateState();
        session().put("linkedin_state", state);

        return LinkedinConnection.generateRedirectUri(state);
    }

    /**
     * Shows the login page
     * @return The login page
     */
    @Secure.Authenticated(User.Type.Guest)
    public static Result login() {
        return ok(
                login.render(generateLinkedinUri(), form(LoginForm.class), form(RegisterForm.class))
        );
    }

    /**
     * Authenticates the user
     * @return Error when authentication fails or redirect if successful
     */
    @Secure.Authenticated(User.Type.Guest)
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
    @Secure.Authenticated(User.Type.Guest)
    public static Result auth(String code, String state, String error, String errorDescription) {
        String sessionState = session().get("linkedin_state");

        // First check the state and errors
        LinkedinConnection linkedinConnectionConnection = LinkedinConnection.fromAccesToken(code);
        if(!state.equals(sessionState) || !error.isEmpty() || code.isEmpty() || linkedinConnectionConnection == null) {
            flash("error", "linkedin.unknownError");
            return redirect(
                    routes.Application.index()
            );
        }

        // Save the current linkedin Connection and get the identity
        linkedinConnectionConnection.toSession();
        LinkedinIdentity identity = LinkedinIdentity.authenticate(linkedinConnectionConnection);

        // Save login to session and redirect to home
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
    @Secure.Authenticated(User.Type.Guest)
    public static Result registration() {
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();

        // Check for errors in register
        if(registerForm.hasErrors()) {
            return badRequest(login.render(generateLinkedinUri(), form(LoginForm.class), registerForm));
        }
        else {
            // Create the new user and identity

            registerForm.get().save();

            flash("success", "authentication.emailSent");
            return redirect(
                    routes.Application.index()
            );
        }
    }

    /**
     * Logs out the current user
     * @return Redirects to the application home page
     */
    @Secure.Authenticated
    public static Result logout() {
        flash("success", "authentication.loggedOut");
        session().clear();
        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Generates an alternative result if the user is not authenticated.
     * @return Redirect to the login page
     */
    public static Result onUnauthorized() {
        flash("error", "authentication.unauthorized");
        return redirect(
                routes.Authentication.login()
        );
    }

    /**
     * Generates an alternative result if the user is authrized and shouldn't be.
     * @return Redirect to the index
     */
    public static Result onAuthorized() {
        flash("error", "authentication.authorized");
        return redirect(
                routes.Application.index()
        );
    }

     /** Checks if the mail and token are valid. If so, the email address is validated
     * @param mail The email address
     * @param token The token
     * @return Redirect to index if params aren't valid, to login otherwise
     */
    public static Result verify(String mail, String token) {
        User user = User.findByEmail(mail);
        if(user == null || token == null) {
            flash("error", "error.unknownValidation");
            return redirect(
                    routes.Application.index()
            );
        }
        else if (!token.equals(user.getToken())) {
            flash("error", "error.wrongToken");
            return redirect(
                    routes.Application.index()
            );
        }
        else {
            user.setToken(null);
            user.save();
            flash("success", "authentication.verified");
            return redirect(
                    routes.Authentication.login()
            );
        }
    }
}
