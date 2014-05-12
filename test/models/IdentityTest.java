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
public class IdentityTest extends WithApplication {

    private User bob;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new user
        bob = new User("bob");
        bob.save();
    }

    @Test
    public void createAndRetrieveIdentity() {
        new Identity(bob, "bob@example.com", "floortje<3").save();
        Identity identity = Ebean.find(Identity.class).where().eq("identifier", "bob@example.com").findUnique();

        assertNotNull(identity);
        assertEquals(bob, identity.getUser());
    }

    @Test
    public void testSuccesAuthenticate() {
        new Identity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = Identity.authenticate("bob@reallybad.com", "floor");
        assertNotNull(identity);
    }

    @Test
    public void testFailEmailAuthenticate() {
        new Identity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = Identity.authenticate("bob@wrongmail.com", "floor");
        assertNull(identity);
    }

    @Test
    public void testFailPasswordAuthenticate() {
        new Identity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = Identity.authenticate("bob@reallybad.com", "wrongpass");
        assertNull(identity);
    }
}
