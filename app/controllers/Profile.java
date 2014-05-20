package controllers;

import forms.ProfileForm;
import helpers.Secure;
import models.*;
import play.mvc.*;
import views.html.profile.*;

import static play.data.Form.form;

/**
 * Created by Freek on 19/05/14.
 * Profile controller to edit, view and manage profiles
 */
@Secure.Authenticated
public class Profile extends Controller {

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
        ProfileForm profileForm = new ProfileForm(Secure.getUser());
        return ok(edit.render(form(ProfileForm.class).fill(profileForm)));
    }
}
