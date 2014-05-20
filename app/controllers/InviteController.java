package controllers;

import helpers.Secure;
import models.*;
import play.mvc.*;
import views.html.*;

/**
 * Created by Marijn Goedegebure on 20-5-2014.
 * The class that handles all the actions used with the invites
 */
public class InviteController extends Controller {

    @Secure.Authenticated
    public static Result viewInvite(long id) {
        Invite invite = Invite.findById(id);
        return ok(viewInvite.render(invite));
    }

    @Secure.Authenticated
    public static Result acceptInvite(long id) {
        Invite invite = Invite.findById(id);
        invite.setAccepted(true);
        invite.save();
        return redirect(routes.PracticalController.viewPractical(invite.getPractical().getId()));
    }

}
