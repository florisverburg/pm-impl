package controllers;

import models.*;
import org.junit.Before;
import org.junit.Test;
import play.mvc.*;
import play.test.WithApplication;
import static org.junit.Assert.*;

import static play.test.Helpers.*;

/**
 * Created by Marijn Goedegebure on 16-5-2014.
 */
public class PracticalControllerTest extends WithApplication {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void testRegisterSuccessfulAddition() {
        Practical practical = Practical.findByName("PracticalControllerTest");
        User userToRegister = User.findByName("DefaultUser1");

        Result result = callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id",
                        userToRegister.getId().toString()));
        Practical resultPractical = Practical.findById(practical.getId());

        assertEquals(SEE_OTHER, status(result));
        assertEquals("practical.successfulAddition", flash(result).get("success"));
    }

    @Test
    public void RegisterNonExisting() {
        Result result = callAction(controllers.routes.ref.PracticalController.register(99999L, "test"),
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
        Practical resultPractical = Practical.findById(practical.getId());

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
