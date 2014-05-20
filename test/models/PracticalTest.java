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
    private User peter;

    private Practical programmingAssignment;
    private Practical documentingAssingment;

    private PracticalGroup bobsGroup;
    private PracticalGroup bobAndHendriksGroup;
    private PracticalGroup hendriksGroup;

    private Invite programmingBobToHendrik;
    private Invite documentingHendrikToBoB;
    private Invite programmingBobToPeter;
    private Invite documentingPeterToHendrik;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new user
        bob = new User("Bob", "Verburg", "bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik", "Tienen", "hendrik@example.com");
        hendrik.save();

        peter = new User("Peter", "Goedegebure", "peter@example.com");
        peter.save();

        // Create a new practical
        programmingAssignment = new Practical("ProgrammingAssignment", "Assignment about programming");
        programmingAssignment.save();

        // Create a new practical
        documentingAssingment = new Practical("DocumentingAssignment", "Assignment about documenting");
        documentingAssingment.save();

        // Create a new practicalGroup
        bobsGroup = new PracticalGroup(programmingAssignment);
        bobsGroup.save();

        // Create a new practicalGroup
        hendriksGroup = new PracticalGroup(programmingAssignment);
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

        // Create a new invite
        programmingBobToHendrik = new Invite(programmingAssignment, bob, hendrik);
        programmingBobToHendrik.save();

        // Create a new invite
        programmingBobToPeter = new Invite(programmingAssignment, bob, peter);
        programmingBobToPeter.save();

        // Create a new invite
        documentingPeterToHendrik = new Invite(documentingAssingment, peter, hendrik);
        documentingPeterToHendrik.save();

        // Create a new invite
        documentingHendrikToBoB = new Invite(documentingAssingment, hendrik, bob);
        documentingHendrikToBoB.save();
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationPractical() {
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
        assertEquals(returnedUsers.get(0).getFirstName(), "Bob");
        assertEquals(returnedUsers.get(1).getFirstName(), "Hendrik");
        assertEquals(returnedUsers.size(), 2);

        // Get the other right
        returnedValue = Practical.findByName("DocumentingAssignment");
        returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(), "Bob");
        assertEquals(returnedUsers.size(), 1);
    }

    /**
     * Method to test the one-to-many relationship of practical with practical group
     */
    @Test
    public void testOneToManyPracticalGroups() {
        // Get practical groups
        Practical returnedValue = Practical.findByName("ProgrammingAssignment");
        List<PracticalGroup> returnedPracticalGroups = returnedValue.getPracticalGroups();

        // check whether associated teams are correct
        assertEquals(returnedPracticalGroups.get(0), bobsGroup);
        assertEquals(returnedPracticalGroups.get(1), hendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 2);

        // Get the other right
        returnedValue = Practical.findByName("DocumentingAssignment");
        returnedPracticalGroups = returnedValue.getPracticalGroups();

        // check whether associated teams are correct
        assertEquals(returnedPracticalGroups.get(0), bobAndHendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 1);
    }

    @Test
    public void testOneToManyInvites() {
        // Get practical groups
        Practical returnedValue = Practical.findByName("ProgrammingAssignment");
        List<Invite> returnedInvites = returnedValue.getInvites();

        // check whether associated teams are correct
        assertEquals(returnedInvites.size(), 2);
        assertEquals(returnedInvites.get(0).getId(), programmingBobToHendrik.getId());
        assertEquals(returnedInvites.get(1).getId(), programmingBobToPeter.getId());

        // Get the other right
        returnedValue = Practical.findByName("DocumentingAssignment");
        returnedInvites = returnedValue.getInvites();

        // check whether associated teams are correct
        assertEquals(returnedInvites.size(), 2);
        assertEquals(returnedInvites.get(0), documentingPeterToHendrik.getId());
        assertEquals(returnedInvites.get(1), documentingHendrikToBoB.getId());
    }
}
