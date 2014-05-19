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
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
        TestHelper.loadYamlFile("test-data.yml");
    }

    @Test
    public void testRegisterSuccessfulAddition() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 2);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id",
                        User.findByEmail("admin@test.com").getId().toString()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 3);
        assertEquals(resultPractical.getUsers().get(2).getFirstName(), "admin");
    }

    @Test
    public void testRegisterAlreadyCoupled() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 2);
        assert(practical.getUsers().contains(practical.getUsers().get(0)));

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()),
                fakeRequest().withSession("user_id", practical.getUsers().get(0).getId().toString()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 2);
    }

    @Test
    public void testRegisterNotAuthenticated() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 2);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(), practical.getSecret()));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 2);
    }

    @Test
    public void testRegisterWrongSecret() {
        Practical practical = Practical.findById(1);
        assertEquals(practical.getUsers().size(), 2);

        callAction(controllers.routes.ref.PracticalController.register(practical.getId(),"NotTheSecret"),
                fakeRequest().withSession("user_id", "3"));
        Practical resultPractical = Practical.findById(1);

        assertEquals(resultPractical.getUsers().size(), 2);
    }

}
