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
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 5);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id",
                        User.findByEmail("admin@example.com").getId().toString()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 6);
        assertEquals(resultPractical.getUsers().get(2).getFirstName(), "DefaultUser2");
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
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 5);
        assert(practical.getUsers().contains(practical.getUsers().get(0)));

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id", practical.getUsers().get(0).getId().toString()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 5);
    }

    @Test
    public void registerNotAuthenticated() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 5);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 5);
    }

    @Test
    public void registerWrongSecret() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 5);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(),"NotTheSecret"),
                fakeRequest().withSession("user_id", "3"));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 5);
    }

}
