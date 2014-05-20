package controllers;

import helpers.Secure;
import models.*;
import play.Logger;
import play.mvc.*;
import views.html.practical.list;
import views.html.practical.view;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 * The class that handles all the practical controller actions
 */
@Secure.Authenticated
public class PracticalController extends Controller {

    /**
     * Method used to register a practical to a user
     * @param id The id of the practical
     * @param secret The secret of the practical
     * @return Shows either the practical page itself or an error
     */
    public static Result register(long id, String secret) {
        Practical practicalToRender = Practical.findById(id);
        User user = Secure.getUser();
        // Checks if practical exist
        if(practicalToRender == null) {
            // When practical does not exist, show correct error
            flash("error", "practical.doesNotExist");
            return redirect(routes.Application.index());
        }
        else if (practicalToRender.getUsers().contains(user)){
            // Checks of the user is not already a part of the practical
            return redirect(routes.PracticalController.view(practicalToRender.getId()));
        }
        else if (!practicalToRender.getSecret().equals(secret)) {
            // Checks if the secret is correct
            flash("error", "practical.wrongSecret");
            return redirect(routes.PracticalController.view(practicalToRender.getId()));
        }
        // if everything is correct, add user to practical
        Practical.addUserToPractical(practicalToRender, Secure.getUser());
        return redirect(routes.PracticalController.view(practicalToRender.getId()));
    }

    /**
     * View the practical specified by the url
     * @param id of the practical
     * @return Show the practical information
     */
    public static Result view(long id) {
        Practical practicalToRender = Practical.findById(id);
        // If practical does not exist
        if (practicalToRender == null) {
            Logger.debug("Practical does not exist");
            flash("error", "practical.doesNotExist");
            return redirect(routes.Application.index());
        }
        // If user is not enrolled to practical, it can't view it
        if (!practicalToRender.getUsers().contains(Secure.getUser())) {
            flash("error", "practical.userIsNotEnrolled");
            return redirect(routes.Application.index());
        }
        else {
            Logger.debug("Practical does exist");
            return ok(view.render(practicalToRender));
            Logger.debug("Practical does exist");
            return ok(viewPractical.render(practicalToRender));
        }
    }

    @Secure.Authenticated
    public static Result sendInvitePracticalGroup(long id) {
        PracticalGroup practicalGroup = PracticalGroup.findById(id);
        User receiver =  practicalGroup.getUsers().get(0);
        User sender = Secure.getUser();
        if(!Invite.sendInvite(practicalGroup.getPractical(), sender, receiver)) {
            Logger.debug("Sending invitation unsuccessful");
            flash("error", "practical.UnsuccessfulSend");
            return redirect(routes.PracticalController.viewPractical(practicalGroup.getPractical().getId()));
        }
        Logger.debug("Successful invitation created");
        flash("success", "practical.InviteSend");
        return redirect(routes.PracticalController.viewPractical(practicalGroup.getPractical().getId()));
    }

    /**
     * List the practicals where the user is registered to
     * @return Shows a list of registered practicals
     */
    public static Result list() {
        User user = Secure.getUser();
        return ok(list.render(user.getPracticals()));
    }

    @Secure.Authenticated
    public static Result viewPracticalGroup(long id) {
        PracticalGroup practicalGroup = PracticalGroup.findById(id);
        if(!Secure.getUser().getPracticals().contains(practicalGroup.getPractical())){
            flash("error", "practical.userIsNotEnrolled");
            return redirect(routes.Application.index());
        }
        return ok(viewPracticalGroup.render(practicalGroup));
    }
}
