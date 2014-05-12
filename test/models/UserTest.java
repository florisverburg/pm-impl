package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import com.avaje.ebean.*;

/**
 * Created by Freek on 12/05/14.
 */
public class UserTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveUser() {
        new User("bob").save();
        User bob = Ebean.find(User.class).where().eq("name", "bob").findUnique();

        assertNotNull(bob);
        assertEquals("bob", bob.getName());
    }

    @Test
    public void authenticateSucces() {
        User floortje = new User("floortje");
        floortje.save();
        new Identity(floortje, "floor@floor.com", "florina").save();

        assertNotNull(User.authenticate("floor@floor.com", "florina"));
        assertEquals(User.authenticate("floor@floor.com", "florina"), floortje);
    }

    @Test
    public void authenticateFail() {
        User floortje = new User("floortje");
        floortje.save();
        new Identity(floortje, "floor@floor.com", "florina").save();

        assertNull(User.authenticate("floor@wrongemail.com", "florina"));
        assertNull(User.authenticate("floor@floor.com", "wrongpass"));
    }
}
