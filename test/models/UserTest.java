package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
 * Simple test that tests the User model
 */
public class UserTest extends WithApplication {

    private User bob;

    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        bob = User.findByName("Bob");
    }

    /**
     * Method to test whether the creation of an user has been successful
     */
    @Test
    public void testCreationUser() {
        // Check the values of the setUp() method
        assertThat(bob.getFirstName()).isEqualTo("Bob");
        assertThat(bob.getLastName()).isEqualTo("Verburg");
        assertThat(bob.getEmail()).isEqualTo("bob@example.com");
    }

    /**
     * Method to test the usage of the user's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        bob.setFirstName("Erica");
        bob.setLastName("Tienen");
        bob.setEmail("erica@example.com");
        bob.save();

        // Check the new values
        assertThat(bob.getFirstName()).isEqualTo("Erica");
        assertThat(bob.getLastName()).isEqualTo("Tienen");
        assertThat(bob.getEmail()).isEqualTo("erica@example.com");
    }

    @Test
    public void getByEmail() {
        User createRob = new User("rob", "lastName", "rob@none.com");
        createRob.save();
        User rob = User.findByEmail("rob@none.com");

        assertNotNull(rob);
        assertEquals(createRob, rob);
    }

    @Test
    public void authenticateSuccess() {
        new PasswordIdentity(bob, bob.getEmail(), "florina").save();

        assertNotNull(User.authenticate(bob.getEmail(), "florina"));
        assertEquals(User.authenticate(bob.getEmail(), "florina"), bob);
    }

    @Test
    public void authenticateFail() {
        new PasswordIdentity(bob, bob.getEmail(), "florina").save();

        assertNull(User.authenticate("floor@wrongemail.com", "florina"));
        assertNull(User.authenticate(bob.getEmail(), "wrongpass"));
    }

    @Test
    public void typeTest() {
        assertEquals(hans.getType(), User.Type.Teacher);
        assertNotEquals(hans.getType(), User.Type.User);
    }

    @Test
    public void testEquals() {
        assertEquals(bob, bob);
        assertNotEquals(bob, hendrik);
    }
}
