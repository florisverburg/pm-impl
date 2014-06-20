package controllers;

import models.*;
import play.mvc.*;
import views.html.practicalGroup.view;

/**
 * Created by Freek on 07/06/14.
 * Practical Group Controller
 */
@Secure.Authenticated
public class PracticalGroupController extends Controller {

    /**
     * Method to view a practical group
     * @param id of the practical group to view
     * @return an ok with a practical group
     */
    public static Result view(long id) {
        PracticalGroup practicalGroup = PracticalGroup.findById(id);

        // Check if the practicalGroup exists
        if(practicalGroup == null) {
            flash("error", "practicalGroup.doesNotExist");
            return redirect(routes.ApplicationController.index());
        }

        // Check if the user is enrolled for the practical
        if(!practicalGroup.getPractical().isEnrolled(Secure.getUser())){
            flash("error", "practical.userIsNotEnrolled");
            return redirect(routes.ApplicationController.index());
        }

        // Find my own practical group
        PracticalGroup ownPracticalGroup = PracticalGroup.findWithPracticalAndUser(
                practicalGroup.getPractical(),
                Secure.getUser());

        return ok(view.render(practicalGroup, ownPracticalGroup));
    }

    /**
     * Method to send an invite to a practical group (you are actually sending an invite
     * to the first user of this group)
     * @param id of the practical group to send to
     * @return a redirect to the view practical
     */
    public static Result sendInvite(long id) {
        User user = Secure.getUser();
        PracticalGroup practicalGroup1 = PracticalGroup.findById(id);

        // Check if we both have a practical group
        if(practicalGroup1 == null) {
            flash("error", "practicalGroup.doesNotExist");
            return redirect(routes.PracticalController.list());
        }
        PracticalGroup practicalGroup2 = PracticalGroup.findWithPracticalAndUser(practicalGroup1.getPractical(), user);
        if(practicalGroup2 == null || !practicalGroup2.getOwner().equals(user)) {
            flash("error", "practicalGroup.notAllowed");
            return redirect(routes.PracticalController.list());
        }

        // Check if the invite was successfully send
        if(Invite.sendInvite(practicalGroup2, practicalGroup1) == null) {
            flash("error", "practical.unsuccessfulSend");
            return redirect(routes.PracticalController.view(practicalGroup1.getPractical().getId()));
        }

        flash("success", "practical.inviteSend");
        return redirect(routes.PracticalController.view(practicalGroup1.getPractical().getId()));
    }

    /**
     * Method to leave a practical group
     * @param id of the user that wants to leave his/her practical group
     * @return redirect to the practical page
     */
    public static Result leave(long id) {
        PracticalGroup practicalGroup = PracticalGroup.findById(id);
        User user = Secure.getUser();

        // Leave group and reset the states of the invites to Rejected
        practicalGroup.leaveGroup(user);
        Invite.rejectOtherInvitesUser(user, practicalGroup.getPractical(), true, Invite.State.Accepted);

        flash("success", "practical.removeGroupMember");
        return redirect(routes.PracticalController.view(practicalGroup.getPractical().getId()));
    }
}
