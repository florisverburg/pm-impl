package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.*;


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
        // Create a new user
        User createdUser = new User ("Laura", "Dragos", "laura@example.com", User.Type.User);
        createdUser.save();

        // Retrieve user from database
        User retrievedUser = User.findById(createdUser.getId());

        // Check the values of the setUp() method
        assertEquals(retrievedUser.getFirstName(), "Laura");
        assertEquals(retrievedUser.getLastName(), "Dragos");
        assertEquals(retrievedUser.getEmail(), "laura@example.com");
        assertEquals(retrievedUser.getType(), "User");
        assertEquals(retrievedUser.getToken().getClass(), String.class);
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
        bob.setType(User.Type.Admin);
        bob.setToken("abc");
        bob.save();

        // Retrieve bob from the database
        User retrievedUser = User.findById(bob.getId());

        // Check the new values
        assertEquals(retrievedUser.getFirstName(), "Erica");
        assertEquals(retrievedUser.getLastName(), "Tienen");
        assertEquals(retrievedUser.getEmail(), "erica@example.com");
        assertEquals(retrievedUser.getType(), "Admin");
        assertEquals(retrievedUser.getToken(), "abc");
    }

    /**
     * Test getByEmailMethod
     */
    @Test
    public void getByEmail() {
        // Test whether the findByEmail returns the correct values
        User createRob = new User("rob", "lastName", "rob@example.com", User.Type.Admin);
        createRob.save();
        User rob = User.findByEmail("rob@none.com");
        // Check the new values
        assertEquals(rob.getFirstName(), "rob");
        assertEquals(rob.getLastName(), "Tienen");
        assertEquals(rob.getEmail(), "rob@example.com");
        assertEquals(rob.getType(), "Admin");


        // retrieve the user rob by his email and by his id
        User retrievedUserById = User.findById(rob.getId());
        User retrievedUserByEmail = User.findByEmail(rob.getEmail());

        // Test whether the two functions return the same data
        assertEquals(retrievedUserById, retrievedUserByEmail);
        assertEquals(retrievedUserById.getId(), retrievedUserByEmail.getId());
        assertEquals(retrievedUserById.getEmail(), retrievedUserByEmail.getEmail());
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
        assertNotEquals(bob.getType(), User.Type.Teacher);
        assertEquals(bob.getType(), User.Type.User);
    }

    @Test
    public void findPendingInvitesTest() {
        //bob.getInvitesReceived()
        //findPendingInvitesUser
    }
}
