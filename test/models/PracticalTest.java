package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 14-5-2014.
 */
public class PracticalTest extends WithApplication {

    private Practical testAssignment;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new practical
        testAssignment = Practical.findByName("Programming");
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationPractical() {
        Practical createdPractical = new Practical("Created Practical", "Created Practical to test the creation of a practical");
        createdPractical.save();
        // Check the values of the setUp() method
        assertEquals(createdPractical.getName(), "Created Practical");
        assertEquals(createdPractical.getDescription(), "Created Practical to test the creation of a practical");
        assertEquals(createdPractical.getSecret().getClass(), String.class);
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        User admin = User.findByEmail("defaultuser1@example.com");
        // Set different values
        testAssignment.setName("setName");
        testAssignment.setDescription("Test description");
        testAssignment.setAdmin(admin);
        testAssignment.save();

        // Check the new values
        assertEquals(testAssignment.getName(), "setName");
        assertEquals(testAssignment.getDescription(), "Test description");
        assertEquals(testAssignment.getAdmin().getId(),admin.getId());
    }

    /**
     * Test findByName
     */
    @Test
    public void testFindByName() {
        // Test whether the findByEmail returns the correct values
        Practical createdPractical = new Practical("Created Practical", "Created Practical to test the creation of a practical");
        createdPractical.save();
        createdPractical = Practical.findByName("Created Practical");
        // Check the values of the setUp() method
        assertEquals(createdPractical.getName(), "Created Practical");
        assertEquals(createdPractical.getDescription(), "Created Practical to test the creation of a practical");

        // retrieve the practical by it's id and by it's email
        Practical retrievedUserById = Practical.findById(createdPractical.getId());
        Practical retrievedUserByName = Practical.findByName(createdPractical.getName());

        // Test whether the two functions return the same data
        assertEquals(retrievedUserById, retrievedUserByName);
        assertEquals(retrievedUserById.getId(), retrievedUserByName.getId());
        assertEquals(retrievedUserById.getName(), retrievedUserByName.getName());
    }

    /**
     * Test isEnrolled
     */
    @Test
    public void testIsEnrolled() {
        // Check whether the assignment has users enrolled
        assert(testAssignment.getUsers().size() > 0);

        // Get an enrolled user
        User enrolledUser = testAssignment.getUsers().get(0);

        assertNotNull(enrolledUser);
        // Check whether the isEnrolled gives back the correct value
        assert(testAssignment.isEnrolled(enrolledUser));

        User notEnrolledUser = new User("NotEnrolled", "User", "notenrolled@example.com", User.Type.User);
        notEnrolledUser.save();
        // Check whether a not enrolled user returns false
        assertFalse(testAssignment.isEnrolled(notEnrolledUser));
    }
}
