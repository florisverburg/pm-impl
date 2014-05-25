package models;

import junit.framework.*;
import org.junit.*;
import org.junit.Test;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
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
}
