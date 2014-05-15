package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 14-5-2014.
 */
public class PracticalTest extends WithApplication {

    private User bob;
    private User hendrik;

    private Practical programmingAssignment;
    private Practical documentingAssingment;

    private PracticalGroup bobsGroup;
    private PracticalGroup bobAndHendriksGroup;
    private PracticalGroup hendriksGroup;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new user
        bob = new User("Bob","Verburg","English","bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik","Tienen","Dutch","hendrik@example.com");
        hendrik.save();

        // Create a new practical
        programmingAssignment = new Practical("ProgrammingAssignment", "Assignment about programming", "programming");
        programmingAssignment.save();

        // Create a new practical
        documentingAssingment = new Practical("DocumentingAssignment", "Assignment about documenting", "documenting");
        documentingAssingment.save();

        // Create a new practicalGroup
        bobsGroup = new PracticalGroup();
        bobsGroup.setPractical(programmingAssignment);
        bobsGroup.save();

        // Create a new practicalGroup
        hendriksGroup = new PracticalGroup();
        hendriksGroup.setPractical(programmingAssignment);
        hendriksGroup.save();

        // Create a new practicalGroup
        bobAndHendriksGroup = new PracticalGroup();
        bobAndHendriksGroup.setPractical(documentingAssingment);
        bobAndHendriksGroup.save();

        // Add users to programming assignment
        programmingAssignment.addUsers(bob);
        programmingAssignment.addUsers(hendrik);
        programmingAssignment.setAdmin(bob);
        programmingAssignment.addPracticalGroup(bobsGroup);
        programmingAssignment.addPracticalGroup(hendriksGroup);
        programmingAssignment.save();

        // Add users to documenting assignment
        documentingAssingment.addUsers(bob);
        documentingAssingment.setAdmin(hendrik);
        documentingAssingment.addPracticalGroup(bobAndHendriksGroup);
        documentingAssingment.save();
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Check the values of the setUp() method
        assertEquals(programmingAssignment.getName(), "ProgrammingAssignment");
        assertEquals(programmingAssignment.getDescription(), "Assignment about programming");
        assertEquals(programmingAssignment.getAdmin().getFirstName(),bob.getFirstName());
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        programmingAssignment.setName("TestValue");
        programmingAssignment.setDescription("Test description");
        programmingAssignment.setAdmin(hendrik);
        programmingAssignment.save();

        // Check the new values
        assertEquals(programmingAssignment.getName(), "TestValue");
        assertEquals(programmingAssignment.getDescription(), "Test description");
        assertEquals(programmingAssignment.getAdmin().getFirstName(),hendrik.getFirstName());
    }

    /**
     * Method to test the many-to-many relations of practical with User
     */
    @Test
    public void testManyToMany() {
        // Get users
        Practical returnedValue = Practical.findByName("ProgrammingAssignment");
        List<User> returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertEquals(returnedUsers.get(1).getFirstName(),"Hendrik");
        assertEquals(returnedUsers.size(), 2);

        // Get the other right
        returnedValue = Practical.findByName("DocumentingAssignment");
        returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertEquals(returnedUsers.size(), 1);
    }

    /**
     * Method to test the one-to-many relationship of practical with practical group
     */
    @Test
    public void testOneToMany() {
        // Get practical groups
        Practical returnedValue = Practical.findByName("ProgrammingAssignment");
        List<PracticalGroup> returnedPracticalGroups = returnedValue.getPracticalGroups();

        // check whether associated teams are correct
        assertEquals(returnedPracticalGroups.get(0),bobsGroup);
        assertEquals(returnedPracticalGroups.get(1),hendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 2);

        // Get the other right
        returnedValue = Practical.findByName("DocumentingAssignment");
        returnedPracticalGroups = returnedValue.getPracticalGroups();

        // check whether associated teams are correct
        assertEquals(returnedPracticalGroups.get(0),bobAndHendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 1);
    }
}
