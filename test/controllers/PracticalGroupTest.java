package controllers;

import models.*;
import org.junit.Before;
import org.junit.Test;
import play.mvc.*;
import play.test.WithApplication;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

/**
 * Created by Freek van Tienen on 16-5-2014.
 */
public class PracticalGroupTest extends WithApplication {

    User user;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        user = User.findByName("Unverified");
    }

    @Test
    public void viewNonExisting() {
        Result result = callAction(routes.ref.PracticalGroupController.view(9999999),
                fakeRequest().withSession("user_id",
                user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practicalGroup.doesNotExist", flash(result).get("error"));
    }

    @Test
    public void viewNotEnrolled() {
        List<PracticalGroup> groups = PracticalGroup.findByPractical(Practical.findByName("PracticalControllerTest"));

        Result result = callAction(routes.ref.PracticalGroupController.view(groups.get(0).getId()),
                fakeRequest().withSession("user_id",
                        user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.userIsNotEnrolled", flash(result).get("error"));
    }

}
