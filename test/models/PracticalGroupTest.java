package models;

import junit.framework.*;
import org.junit.*;
import org.junit.Test;
import play.Logger;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 */
public class PracticalGroupTest extends WithApplication {

    private PracticalGroup testPracticalGroup;
    private Practical testPractical;
    private User testUser;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testPractical = Practical.findByName("Programming");
        testUser = User.findByName("DefaultUser3");
        testPracticalGroup = PracticalGroup.findWithPracticalAndUser(testPractical, testUser);
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Create Practical Group
        PracticalGroup createdPracticalGroup = new PracticalGroup(testPractical, testUser);
        createdPracticalGroup.save();

        createdPracticalGroup = PracticalGroup.findById(createdPracticalGroup.getId());

        // Check the values of the constructor and findbyid method
        assertEquals(createdPracticalGroup.getPractical(), testPractical);
        assertNotNull(createdPracticalGroup.getGroupMembers());
        assertEquals(createdPracticalGroup.getGroupMembers().size(), 0);
        assertEquals(createdPracticalGroup.getOwner().getId(), testUser.getId());
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        Practical otherPractical = Practical.findByName("Documenting");
        User otherUser = User.findByName("DefaultUser2");

        // Set different values
        testPracticalGroup.setPractical(otherPractical);
        testPracticalGroup.setOwner(otherUser);
        testPractical.save();

        // Check the new values
        assertEquals(testPracticalGroup.getPractical().getId(), otherPractical.getId());
        assertEquals(testPracticalGroup.getOwner().getId(), otherUser.getId());
    }

    /**
     * Method to test findWithPracticalAndUser
     */
    @Test
    public void testFindWithPracticalAndUser() {
        // Create the users to put into the practicalgroup
        Practical createdPractical = new Practical("Created Practical", "Practical that has been created");
        createdPractical.save();
        User createdUser1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        createdUser1.save();
        User createdUser2 = new User("CreatedUser2", "LastName", "createduser@example.com", User.Type.User);
        createdUser2.save();
        // Save the users to the practical
        createdPractical.addUser(createdUser1);
        createdPractical.addUser(createdUser2);
        createdPractical.save();


        // Create the first practicalGroup
        PracticalGroup createdPracticalGroup = new PracticalGroup(createdPractical, createdUser1);
        createdPracticalGroup.save();
        createdUser1.save();
        createdPractical.save();

        // Check the returned values of the method
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser1).getId(), createdPracticalGroup.getId());
        assertNull(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser2));

        // Add a groupmember to the group
        createdPracticalGroup.addGroupMember(createdUser2);
        createdPracticalGroup.save();
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser1).getId(), createdPracticalGroup.getId());
        // Check if you can find the group by one of its groupmembers
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser2).getId(), createdPracticalGroup.getId());
    }
}
