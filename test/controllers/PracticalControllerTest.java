package controllers;

import models.*;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.mvc.*;
import play.test.WithApplication;
import static org.junit.Assert.*;

import static play.test.Helpers.*;

/**
 * Created by Marijn Goedegebure on 16-5-2014.
 */
public class PracticalControllerTest extends WithApplication {

    User user;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        user = User.findByName("Unverified");
    }

    @Test
    public void viewNonExisting() {
        Result result = callAction(controllers.routes.ref.PracticalController.view(9999999),
                fakeRequest().withSession("user_id",
                user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.doesNotExist", flash(result).get("error"));
    }

    @Test
    public void viewNotEnrolled() {
        Practical practical = Practical.findByName("PracticalControllerTest");

        Result result = callAction(controllers.routes.ref.PracticalController.view(practical.getId()),
                fakeRequest().withSession("user_id",
                        user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.userIsNotEnrolled", flash(result).get("error"));
    }

    @Test
    public void viewUser() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User user2 = User.findByName("DefaultUser2");

        Result result = callAction(controllers.routes.ref.PracticalController.view(practical.getId()),
                fakeRequest().withSession("user_id",
                        user2.getId().toString()));

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(practical.getName()));
        assertTrue(contentAsString(result).contains(practical.getDescription()));
        assertFalse(contentAsString(result).contains(practical.getSecret()));
    }

    @Test
    public void viewAdmin() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User user2 = User.findByName("Teacher");

        Result result = callAction(controllers.routes.ref.PracticalController.view(practical.getId()),
                fakeRequest().withSession("user_id",
                        user2.getId().toString()));

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(practical.getName()));
        assertTrue(contentAsString(result).contains(practical.getDescription()));
        assertTrue(contentAsString(result).contains(practical.getSecret()));
    }

    @Test
    public void listUser() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User user2 = User.findByName("DefaultUser2");

        Result result = callAction(controllers.routes.ref.PracticalController.list(),
                fakeRequest().withSession("user_id",
                        user2.getId().toString()));

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(practical.getName()));
        assertTrue(contentAsString(result).contains(practical.getDescription()));
        assertFalse(contentAsString(result).contains("Create a new practical"));
    }

    @Test
    public void listAdmin() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User user2 = User.findByName("Teacher");

        Result result = callAction(controllers.routes.ref.PracticalController.list(),
                fakeRequest().withSession("user_id",
                        user2.getId().toString()));

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(practical.getName()));
        assertTrue(contentAsString(result).contains(practical.getDescription()));
        assertTrue(contentAsString(result).contains("Create a new practical"));
    }

    @Test
    public void deleteNonExisting() {
        Result result = callAction(controllers.routes.ref.PracticalController.delete(9999999),
                fakeRequest().withSession("user_id",
                        user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.doesNotExist", flash(result).get("error"));
    }

    @Test
    public void deleteNoAdmin() {
        Practical practical = Practical.findByName("PracticalControllerTest");

        Result result = callAction(controllers.routes.ref.PracticalController.delete(practical.getId()),
                fakeRequest().withSession("user_id",
                        user.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.isNoAdmin", flash(result).get("error"));
    }

    @Test
    public void delete() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User user2 = User.findByName("Teacher");

        Result result = callAction(controllers.routes.ref.PracticalController.delete(practical.getId()),
                fakeRequest().withSession("user_id",
                        user2.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.deleted", flash(result).get("success"));
        assertNull(Practical.findByName("PracticalControllerTest"));
    }


    @Test
    public void registerSuccessfulAddition() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User userToRegister = User.findByName("DefaultUser1");

        Result result = callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id",
                        userToRegister.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.successfulAddition", flash(result).get("success"));
    }

    @Test
    public void registerNonExisting() {
        Result result = callAction(controllers.routes.ref.PracticalController.register(99999L, "info"),
                fakeRequest().withSession("user_id",
                        User.findByEmail("admin@example.com").getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.doesNotExist", flash(result).get("error"));
    }

    @Test
    public void registerAlreadyCoupled() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User userToRegister = User.findByName("DefaultUser2");

        Result result = callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id",
                        userToRegister.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.alreadyCoupled", flash(result).get("error"));
    }

    @Test
    public void registerNotAuthenticated() {
        Practical practical = Practical.findByName("PracticalControllerTest");

        Result result = callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("authentication.unauthorized", flash(result).get("error"));
    }

    @Test
    public void registerWrongSecret() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User userToRegister = User.findByName("DefaultUser1");

        Result result = callAction(controllers.routes.ref.PracticalController.register(practical.getId(), "wrongSecret"),
                fakeRequest().withSession("user_id",
                        userToRegister.getId().toString()));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.wrongSecret", flash(result).get("error"));
    }

}
