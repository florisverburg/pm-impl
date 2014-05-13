package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

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
        new PasswordIdentity(bob, "bob@example.com", "floortje<3").save();
        Identity identity = Identity.find.where().eq("identifier", "bob@example.com").findUnique();

        assertNotNull(identity);
        assertEquals(bob, identity.getUser());
    }

    @Test
    public void authenticateSuccess() {
        new PasswordIdentity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = PasswordIdentity.authenticate("bob@reallybad.com", "floor");
        assertNotNull(identity);
    }

    @Test
    public void authenticateFailEmail() {
        new PasswordIdentity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = PasswordIdentity.authenticate("bob@wrongmail.com", "floor");
        assertNull(identity);
    }

    @Test
    public void authenticateFailPassword() {
        new PasswordIdentity(bob, "bob@reallybad.com", "floor").save();

        Identity identity = PasswordIdentity.authenticate("bob@reallybad.com", "wrongpass");
        assertNull(identity);
    }
}
