package controllers;

import forms.LoginForm;
import forms.RegisterForm;
import helpers.LinkedinConnection;
import models.*;
import play.Play;
import play.mvc.*;
import play.data.*;
import views.html.*;
import static play.data.Form.*;
import com.typesafe.plugin.*;

/**
 * Created by Freek on 12/05/14.
 * This class handles all the authentication methods like login and register.
 */
public class AuthenticationController extends Controller {

    /**
     * The email address where mails are send from
     */
    private static final String EMAIL_FROM = Play.application().configuration().getString("email.address");

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
                login.render(
                        generateLinkedinUri(),
                        form(LoginForm.class, LoginForm.Login.class),
                        form(RegisterForm.class, LoginForm.Login.class)
                )
        );
    }

    /**
     * Authenticates the user
     * @return Error when authentication fails or redirect if successful
     */
    @Secure.Authenticated(User.Type.Guest)
    public static Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class, LoginForm.Login.class).bindFromRequest();

        // Check for errors in login
        if(loginForm.hasErrors()) {
            return badRequest(login.render(
                    generateLinkedinUri(),
                    loginForm,
                    form(RegisterForm.class, LoginForm.Login.class)
            ));
        }

        // Save login to session and redirect to home
        return authenticated(loginForm.get().getUser());
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
                    routes.ApplicationController.index()
            );
        }

        // Save the current linkedin Connection and get the identity
        linkedinConnectionConnection.toSession();
        LinkedinIdentity identity = LinkedinIdentity.authenticate(linkedinConnectionConnection);

        // Save login to session and redirect to home
        return authenticated(identity.getUser());
    }

    /**
     * When the user is authenticated set the session
     * @param user The user that is logged in
     * @return The home page or profile page
     */
    private static Result authenticated(User user) {
        session().clear();
        session("user_id", user.getId().toString());
        flash("success", "authentication.loggedIn");

        // Redirect to skills if not set
        if(user.getSkillValues().size() <= 0) {
            return redirect(routes.ProfileController.editSkills());
        }
        return redirect(
                routes.ApplicationController.index()
        );
    }

    /**
     * Register a new user
     * @return Error when registration isn't complete else a redirect
     */
    @Secure.Authenticated(User.Type.Guest)
    public static Result registration() {
        Form<RegisterForm> registerForm = form(RegisterForm.class, LoginForm.Login.class).bindFromRequest();

        // Check for errors in register
        if(registerForm.hasErrors()) {
            return badRequest(login.render(
                    generateLinkedinUri(),
                    form(LoginForm.class, LoginForm.Login.class),
                    registerForm
            ));
        }
        else {
            // Create the new user and identity
            User user = registerForm.get().save();
            sendVerification(user);

            flash("success", "authentication.emailSent");
            return redirect(
                    routes.ApplicationController.index()
            );
        }
    }

    /**
     * Sends an email with verification link
     * @param user The user to send the verification to
     */
    private static void sendVerification(User user) {
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject("APMatch - Verify your mail");
        mail.setRecipient(user.getFullName() + " <" + user.getEmail() + ">");
        mail.setFrom("APMatch <" + EMAIL_FROM + ">");
        //sends text/text
        String link = routes.AuthenticationController.verify(user.getEmail(), user.getToken()).absoluteURL(
                false, Http.Context.current()._requestHeader());
        String message = "Verify your account by opening this link: " + link;

        // Avoid testing accounts
        if(!user.getEmail().contains("@example.com")) {
            mail.send(message);
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
                routes.ApplicationController.index()
        );
    }

    /**
     * Generates an alternative result if the user is not authenticated.
     * @return Redirect to the login page
     */
    public static Result onUnauthorized() {
        flash("error", "authentication.unauthorized");
        return Results.redirect(
                routes.AuthenticationController.login()
        );
    }

    /**
     * Generates an alternative result if the user is authorized and shouldn't be.
     * @return Redirect to the index
     */
    public static Result onAuthorized() {
        flash("error", "authentication.authorized");
        return redirect(
                routes.ApplicationController.index()
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
                    routes.ApplicationController.index()
            );
        }
        else if (!token.equals(user.getToken())) {
            flash("error", "error.wrongToken");
            return redirect(
                    routes.ApplicationController.index()
            );
        }
        else {
            user.setToken(null);
            user.save();
            flash("success", "authentication.verified");
            return Results.redirect(
                    routes.AuthenticationController.login()
            );
        }
    }
}
