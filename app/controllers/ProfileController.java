package controllers;

import forms.ProfileForm;
import models.*;
import play.data.*;
import play.data.validation.*;
import play.mvc.*;

import views.html.profile.edit;
import views.html.profile.view;

import static play.data.Form.form;

/**
 * Created by Freek on 19/05/14.
 * Profile controller to edit, view and manage profiles
 */
@Secure.Authenticated
public class ProfileController extends Controller {

    /**
     * Shows the current users' view
     * @return The view page
     */
    public static Result view() {
        User user = Secure.getUser();
        return ok(view.render(user));
    }

    /**
     * Shows the editable profile page
     * @return The edit page
     */
    public static Result edit() {
        User user = Secure.getUser();
        return ok(edit.render(form(ProfileForm.class, ProfileForm.All.class)
                .fill(new ProfileForm(user)), user.hasPassword()));
    }

    /**
     * Shows the editable profile page
     * @return The edit page
     */
    public static Result save() {
        User user = Secure.getUser();
        Form<ProfileForm> profileForm = form(ProfileForm.class, ProfileForm.All.class).bindFromRequest();

        // Check for errors in the profile
        if(profileForm.hasErrors()) {
            return badRequest(edit.render(profileForm, user.hasPassword()));
        }
        else if(!profileForm.get().getEmail().equals(user.getEmail())
                && User.findByEmail(profileForm.get().getEmail()) != null) {
            // Check for double email, need to be checked here
            profileForm.reject(new ValidationError("email", "error.doubleEmail"));
            return badRequest(edit.render(profileForm, user.hasPassword()));
        }
        else {
            // Save the user
            profileForm.get().updateUser(user);
            flash("success", "profile.saved");
            return redirect(routes.ProfileController.view());
        }
    }
}
