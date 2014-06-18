package controllers;

import play.mvc.*;
import views.html.*;

/**
 * The application controller
 */
public class ApplicationController extends Controller {

    /**
     * Create the index page
     * @return The index page
     */
    public static Result index() {
        return ok(index.render("Welcome to APMatch!"));
    }

    /**
     * Create the about page
     * @return The about page
     */
    public static Result about() {
        return ok(about.render("About APMatch"));
    }

    /**
     * Create the contact page with a form to email to this mail address
     * @return The contact page
     */
    public static Result contact() {
        return ok(contact.render(
                "Do you have a comment or question? Please send us an e-mail.",
                "contact@apmatch.nl"));
    }

    /**
     * Create the how does it work page
     * @return the how does it work page
     */
    public static Result howDoesItWork() {
        return ok(howDoesItWork.render("This page describes how different parts of the system work."));
    }
}
