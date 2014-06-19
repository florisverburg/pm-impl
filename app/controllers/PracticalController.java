package controllers;

import com.avaje.ebean.Ebean;
import forms.PracticalForm;
import models.*;
import play.data.*;
import play.mvc.*;
import views.html.practical.list;
import views.html.practical.view;
import views.html.practical.admin;
import views.html.practical.invites;

import static play.data.Form.form;

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
        if(practicalToRender == null) {
            // When practical does not exist, show correct error
            flash("error", "practical.doesNotExist");
            return redirect(routes.ApplicationController.index());
        }
        else if (practicalToRender.getUsers().contains(user)){
            // Checks of the user is not already a part of the practical
            flash("error", "practical.alreadyCoupled");
            return redirect(routes.PracticalController.view(practicalToRender.getId()));
        }
        else if (!practicalToRender.getSecret().equals(secret)) {
            // Checks if the secret is correct
            flash("error", "practical.wrongSecret");
            return redirect(routes.PracticalController.view(practicalToRender.getId()));
        }
        // if everything is correct, add user to practical
        practicalToRender.addUser(Secure.getUser());
        practicalToRender.save();
        PracticalGroup newPracticalGroup = new PracticalGroup(practicalToRender, Secure.getUser());
        newPracticalGroup.save();
        flash("success", "practical.successfulAddition");
        return redirect(routes.PracticalController.view(practicalToRender.getId()));
    }

    /**
     * View the practical specified by the url
     * @param id of the practical
     * @return Show the practical information
     */
    public static Result view(long id) {
        User user = Secure.getUser();
        Practical practical = Practical.findById(id);

        // If practical does not exist
        if(practical == null) {
            flash("error", "practical.doesNotExist");
            return redirect(routes.PracticalController.list());
        }
        // If user is not enrolled to practical, it can't view it
        if(!practical.isEnrolled(user)) {
            flash("error", "practical.userIsNotEnrolled");
            return redirect(routes.PracticalController.list());
        }
        // Check if I'm the admin of the course
        if(practical.isAdmin(user)) {
            Form<PracticalForm> practicalForm = form(PracticalForm.class, PracticalForm.All.class)
                    .fill(new PracticalForm(practical));
            return ok(admin.render(practical, practicalForm));
        }

        return ok(view.render(user, practical));
    }

    /**
     * View the invites specified by the url
     * @param id identity of the practical
     * @return Show the invites for this practical
     */
    public static Result invites(long id) {
        User user = Secure.getUser();
        Practical practical = Practical.findById(id);

        // Check if practical does exist
        if(practical == null) {
            flash("error", "practical.doesNotExist");
            return redirect(routes.PracticalController.list());
        }

        return ok(invites.render(user, practical));
    }

    /**
     * List the practicals where the user is registered to
     * @return Shows a list of registered practicals
     */
    public static Result list() {
        User user = Secure.getUser();
        Form<PracticalForm> practicalForm = null;

        // Check if the user is a teacher then show a form
        if(Secure.isTeacher()) {
            practicalForm = form(PracticalForm.class, PracticalForm.Registration.class);
        }

        return ok(list.render(Practical.findByUser(user), practicalForm));
    }

    /**
     * Edit a practical
     * @param id The id of the practical to edit
     * @return Whether the practical is successfully edited
     */
    @Secure.Authenticated({User.Type.Admin, User.Type.Teacher})
    public static Result edit(long id) {
        User user = Secure.getUser();
        Practical practical = Practical.findById(id);

        // If practical does not exist
        if(practical == null) {
            flash("error", "practical.doesNotExist");
            return redirect(routes.PracticalController.list());
        }
        // If user is not enrolled to practical, it can't view it
        if(!practical.isAdmin(user)) {
            flash("error", "practical.userIsNotEnrolled");
            return redirect(routes.PracticalController.list());
        }

        Form<PracticalForm> practicalForm = form(PracticalForm.class, PracticalForm.All.class)
                .bindFromRequest();
        if(practicalForm.hasErrors()) {
            return badRequest(admin.render(practical, practicalForm));
        }

        practicalForm.get().save(practical);
        flash("success", "practical.edited");
        return redirect(routes.PracticalController.view(id));
    }

    /**
     * Create a new practical
     * @return Redirects to the new practical or errors
     */
    @Secure.Authenticated({User.Type.Admin, User.Type.Teacher})
    public static Result create() {
        User user = Secure.getUser();
        Form<PracticalForm> practicalForm = form(PracticalForm.class).bindFromRequest();

        // Check for errors in the form
        if(practicalForm.hasErrors()) {
            return ok(list.render(Practical.findByUser(user), practicalForm));
        }

        // Else save and view the practical
        Practical practical = practicalForm.get().save(user);
        return redirect(routes.PracticalController.view(practical.getId()));
    }

    /**
     * Delete the practical with a certain id
     * @param id The id of the practical
     * @return Whether the practical was successfully deleted
     */
    public static Result delete(long id) {
        Practical practical = Practical.findById(id);

        // If practical does not exist
        if(practical == null) {
            flash("error", "practical.doesNotExist");
            return redirect(routes.PracticalController.list());
        }
        // Check if the user isn't the admin
        if(!practical.isAdmin(Secure.getUser())) {
            flash("error", "practical.isNoAdmin");
            return redirect(routes.PracticalController.list());
        }

        // Delete the practical
        Ebean.delete(PracticalGroup.findByPractical(practical));
        Ebean.delete(SkillValuePractical.findByPractical(practical));
        practical.delete();
        flash("success", "practical.deleted");
        return redirect(routes.PracticalController.list());
    }
}
