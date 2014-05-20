package models;

import helpers.LinkedinConnection;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import static play.test.Helpers.*;
import static org.mockito.Mockito.*;

/**
 * Created by Freek on 12/05/14.
 */
public class IdentityTest extends WithApplication {

    private User bob;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new user
        bob = new User("Bob","Verburg","bob@example.com");
        bob.save();
    }

    @Test
    public void createAndRetrieveIdentity() {
        Identity create = new PasswordIdentity(bob, "bob@example.com", "floortje<3");
        create.save();
        PasswordIdentity identity = (PasswordIdentity) PasswordIdentity.find.where().eq("email", "bob@example.com").findUnique();

        assertNotNull(identity);
        assertEquals(create, identity);
        assertEquals(bob, identity.getUser());
        assertEquals("bob@example.com", identity.getEmail());
        assertNotNull(identity.getId());
        assertTrue(identity.getId() > 0);
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

    @Test
    public void createLinkedinIdentity() {
        new LinkedinIdentity(bob, "linkedinPersonId").save();
        LinkedinIdentity identity = LinkedinIdentity.byPersonId("linkedinPersonId");

        assertNotNull(identity);
        assertNull(LinkedinIdentity.byPersonId("invalidPersonId"));
        assertEquals("linkedinPersonId", identity.getPersonId());
        assertEquals(bob, identity.getUser());
    }

    @Test
    public void updateLinkedinIdentity() {
        new LinkedinIdentity(bob, "linkedinPersonId").save();
        LinkedinIdentity identity = LinkedinIdentity.byPersonId("linkedinPersonId");

        identity.setPersonId("newPersonId");

        assertNotNull(identity);
        assertEquals("newPersonId", identity.getPersonId());
    }

    @Test
    public void authenticateLinkedinSuccess() {
        new LinkedinIdentity(bob, "linkedinPersonId").save();
        LinkedinConnection linkedinConnection = mock(LinkedinConnection.class);
        when(linkedinConnection.getPersonId()).thenReturn("linkedinPersonId");

        LinkedinIdentity identity = LinkedinIdentity.authenticate(linkedinConnection);

        assertNotNull(identity);
        assertEquals("linkedinPersonId", identity.getPersonId());
        assertEquals(bob, identity.getUser());
    }

    @Test
    public void authenticateLinkedinCreate() {
        LinkedinConnection linkedinConnection = mock(LinkedinConnection.class);
        when(linkedinConnection.getPersonId()).thenReturn("wrongLinkedin");
        when(linkedinConnection.createUser()).thenReturn(bob);
        when(linkedinConnection.createIdentity(bob)).thenReturn(new LinkedinIdentity(bob, "wrongLinkedin"));

        LinkedinIdentity identity = LinkedinIdentity.authenticate(linkedinConnection);

        assertNotNull(identity);
        assertEquals("wrongLinkedin", identity.getPersonId());
        assertEquals(bob, identity.getUser());
    }
}
