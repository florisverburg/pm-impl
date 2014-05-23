package controllers;

import helpers.Secure;
import models.*;
import play.mvc.*;
import views.html.invite.view;

/**
 * Created by Marijn Goedegebure on 20-5-2014.
 * The class that handles all the actions used with the invites
 */
@Secure.Authenticated
public class InviteController extends Controller {

    /**
     * Method to view an invite
     * @param id of the invite to view
     * @return an ok to the viewInvite view
     */
    public static Result view(long id) {
        Invite invite = Invite.findById(id);
        return ok(view.render(invite));
    }

    /**
     * Method to accept an invite
     * @param id of the invite to accept
     * @return redirect to the view practical
     */
    public static Result acceptInvite(long id) {
        Invite invite = Invite.findById(id);
        invite.accept();
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    /**
     * Method to withdraw an invite
     * @param id of the invite to withdraw
     * @return redirect to the view practical
     */
    public static Result withdrawInvite(long id) {
        Invite invite = Invite.findById(id);
        invite.withdraw();
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    /**
     * Method to resend invite, when it is withdrawn.
     * @param id of the invite to resend
     * @return redirect to the view practical
     */
    public static Result resendInvite(long id) {
        Invite invite = Invite.findById(id);
        invite.resend();
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    /**
     * Method to reject an invite
     * @param inviteId of the invite to be rejected
     * @param userId that wants to reject the invite
     * @return redirect to the view practical
     */
    public static Result rejectInvite(long inviteId, long userId) {
        Invite invite = Invite.findById(inviteId);
        User user = User.findById(userId);
        invite.reject(user);
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }
}