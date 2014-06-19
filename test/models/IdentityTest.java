package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
        jsonObject.put("emailAddress", "linkedin@example.com");
        jsonObject.put("firstName", "Test");
        jsonObject.put("lastName", "Linkedin");

        LinkedinConnection linkedinConnection = mock(LinkedinConnection.class);
        when(linkedinConnection.getPersonId()).thenReturn("wrongLinkedin");
        when(linkedinConnection.getPerson()).thenReturn(jsonObject);

        LinkedinIdentity identity = LinkedinIdentity.authenticate(linkedinConnection);

        assertNotNull(identity);
        assertEquals("wrongLinkedin", identity.getPersonId());
        assertNotNull(identity.getUser());
        assertEquals("Test", identity.getUser().getFirstName());
        assertEquals("Linkedin", identity.getUser().getLastName());
    }
}
