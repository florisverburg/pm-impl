package models;

import org.junit.*;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 */
public class PracticalGroupTest extends WithApplication {

    private User bob;
    private User hendrik;

    private PracticalGroup bobsGroup;
    private PracticalGroup bobAndHendriksGroup;

    private Practical programmingAssignment;
    private Practical documentingAssingment;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        // Create a new user
        bob = new User("Bob", "Verburg", "bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik", "Tienen", "hendrik@example.com");
        hendrik.save();

        // Create a new practicalGroup
        bobsGroup = new PracticalGroup();
        bobsGroup.save();

        // Create a new practicalGroup
        bobAndHendriksGroup = new PracticalGroup();
        bobAndHendriksGroup.save();

        // Create a new practical
        programmingAssignment = new Practical("ProgrammingAssignment", "Assignment about programming");
        programmingAssignment.save();

        // Create a new practical
        documentingAssingment = new Practical("DocumentingAssignment", "Assignment about documenting");
        documentingAssingment.save();

        // Add users to the practicalGroup
        bobsGroup.addUser(bob);
        bobsGroup.setPractical(programmingAssignment);
        bobsGroup.save();

        // Add users to the practicalGroup
        bobAndHendriksGroup.addUser(bob);
        bobAndHendriksGroup.addUser(hendrik);
        bobAndHendriksGroup.setPractical(programmingAssignment);
        bobAndHendriksGroup.save();
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Check the values of the setUp() method
        assertEquals(bobsGroup.getId(), PracticalGroup.findById(bobsGroup.getId()).getId());
        assertEquals(bobsGroup.getPractical(), programmingAssignment);
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        bobsGroup.setId(200);
        bobsGroup.setPractical(documentingAssingment);
        List<User> hendriksGroupList = new ArrayList<User>();
        hendriksGroupList.add(hendrik);
        bobsGroup.setUsers(hendriksGroupList);
        programmingAssignment.save();

        // Check the new values
        assertEquals(bobsGroup.getId(), 200);
        assertEquals(bobsGroup.getPractical(), documentingAssingment);
        assertEquals(bobsGroup.getUsers().size(), 1);
    }

    /**()
     * Method to test the many-to-many relations of PracticalGroup with User
     */
    @Test
    public void testManyToMany() {
        // Get practicalGroup
        PracticalGroup returnedValue = PracticalGroup.findById(bobAndHendriksGroup.getId());
        List<User> returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(), "Bob");
        assertEquals(returnedUsers.get(1).getFirstName(), "Hendrik");
        assertEquals(returnedUsers.size(), 2);

        // Get the other right
        returnedValue = PracticalGroup.findById(bobsGroup.getId());
        returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(), "Bob");
        assertEquals(returnedUsers.size(), 1);
    }
}
