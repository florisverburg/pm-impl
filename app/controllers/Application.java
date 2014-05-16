package controllers;

import play.mvc.*;
import views.html.*;

/**
 * The application controller
 */
public class Application extends Controller {

    /**
     * Create the index page
     * @return The index page
     */
    public static Result index() {
        String userId = session("user_id");
        return ok(index.render("Welcome to APMatch!", userId));
    }

    /**
     * Create the about page
     * @return The about page
     */
    public static Result about() {
        String userId = session("user_id");
        return ok(about.render("About APMatch", userId));
    }

    /**
     * Create the contact page with a form to email to this mail address
     * @return The contact page
     */
    public static Result contact() {
        String userId = session("user_id");
        return ok(contact.render(
                "Do you have a comment or question? Please send us an e-mail.",
                "contact@apmatch.nl", userId));
    }



}
