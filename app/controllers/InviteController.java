package controllers;

import forms.MessageForm;
import forms.RegisterForm;
import helpers.Secure;
import models.*;
import play.data.*;
import play.mvc.*;
import views.html.invite.view;
import static play.data.Form.*;

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
        return ok(view.render(invite, form(MessageForm.class)));
    }

    /**
     * Method to accept an invite
     * @param id of the invite to accept
     * @return redirect to the view practical
     */
    public static Result acceptInvite(long id) {
        Invite invite = Invite.findById(id);
        invite.accept();
        flash("success", "invite.accepted");
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
        flash("success", "invite.withdrawn");
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    /**
     * Method to resend invite, when it is withdrawn.
     * @param id of the invite to resend
     * @return redirect to the view practical
     */
    public static Result resendInvite(long id) {
        Invite invite = Invite.findById(id);
        User user = Secure.getUser();
        invite.resend(user);
        flash("success", "invite.resend");
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    /**
     * Method to reject an invite
     * @param inviteId of the invite to be rejected
     * @return redirect to the view practical
     */
    public static Result rejectInvite(long inviteId) {
        Invite invite = Invite.findById(inviteId);
        invite.reject();
        flash("success", "invite.reject");
        return redirect(routes.PracticalController.view(invite.getPractical().getId()));
    }

    @Secure.Authenticated
    public static Result sendMessage(long inviteId, long userId) {
        User user = User.findById(userId);
        Form<MessageForm> messageForm = form(MessageForm.class).bindFromRequest();
        Invite invite = Invite.findById(inviteId);
        if(messageForm.hasErrors()) {
            flash("error", "message.empty");
            return badRequest(view.render(invite, form(MessageForm.class)));
        }
        else {
            Message message = new Message(invite, user, messageForm.get().getMessage());
            message.save();
            flash("success", "message.sent");
            return redirect(
                    routes.InviteController.view(inviteId)
            );
        }
    }
}
