package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {

    /**
     * Create the index page
     * @return The index page
     */
    public static Result index() {
       return ok(index.render("Your new application is ready."));
    }

}
