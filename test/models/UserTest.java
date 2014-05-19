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
    private User hendrik;

    private Skill programming;
    private Skill documenting;

    private Practical programmingAssignment;
    private Practical documentingAssingment;

    private PracticalGroup bobsGroup;
    private PracticalGroup bobAndHendriksGroup;
    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        // Create a new user
        bob = new User("Bob", "Verburg", "bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik", "Tienen", "hendrik@example.com");
        hendrik.save();

        // Create a new skill
        programming = new Skill("Programming", Skill.Type.PROGRAMMING,10);
        programming.save();

        // Create a new skill
        documenting = new Skill("Documenting", Skill.Type.DOCUMENTING,10);
        documenting.save();

        // Create a new practical
        programmingAssignment = new Practical("ProgrammingAssignment", "Assignment about programming");
        programmingAssignment.save();
        programmingAssignment.setAdmin(bob);

        // Create a new practical
        documentingAssingment = new Practical("DocumentingAssignment", "Assignment about documenting");
        documentingAssingment.setAdmin(bob);
        documentingAssingment.save();

        // Create a new practicalGroup
        bobsGroup = new PracticalGroup();

        // Create a new practicalGroup
        bobAndHendriksGroup = new PracticalGroup();

        // Add skills to user bob
        bob.addSkill(programming);
        bob.addSkill(documenting);
        // Add practicals to user bob
        bob.addPractical(programmingAssignment);
        bob.addPractical(documentingAssingment);
        // Add practicals to user bob as admin
        bob.addPracticalAdmin(programmingAssignment);
        bob.addPracticalAdmin(documentingAssingment);
        // Add practicalGroups to user bob
        bob.addPracticalGroup(bobsGroup);
        bob.addPracticalGroup(bobAndHendriksGroup);
        bob.save();

        // Add skills to user hendrik
        hendrik.addSkill(programming);
        // Add practicals to user hendrik
        hendrik.addPractical(programmingAssignment);
        // Add practicals to user hendrik as admin
        hendrik.addPracticalAdmin(programmingAssignment);
        // Add practicalGroups to user hendrik
        hendrik.addPracticalGroup(bobAndHendriksGroup);
        hendrik.save();

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

    /**
     * Method to test the many-to-many relationship of User with Skills and Teams
     */
    @Test
    public void testManyToMany() {
        // Find user in table User
        bob = User.findByName("Bob");
        // Check for skills that bob has
        List<Skill> bobsSkills = bob.getSkills();
        assertThat(bobsSkills.get(0).getName()).isEqualTo("Programming");
        assertThat(bobsSkills.get(1).getName()).isEqualTo("Documenting");
        assertThat(bobsSkills.size()).isEqualTo(2);

        // Check for Teams that bob has
    }

    /**
     * Method to test the many-to-many relations of User with Skill
     */
    @Test
    public void testManyToManySkills() {
        // Get user
        User returnedValue = User.findByName("Bob");
        List<Skill> returnedSkills = returnedValue.getSkills();

        // Check whether associated skills are correct
        assertThat(returnedSkills.get(0).getName()).isEqualTo("Programming");
        assertThat(returnedSkills.get(1).getName()).isEqualTo("Documenting");
        assertThat(returnedSkills.size()).isEqualTo(2);

        // Get other user
        returnedValue = User.findByName("Hendrik");
        returnedSkills = returnedValue.getSkills();

        // Check whether associated skills are correct
        assertThat(returnedSkills.get(0).getName()).isEqualTo("Programming");
        assertThat(returnedSkills.size()).isEqualTo(1);
    }

    /**
     * Method to test the many-to-many relations of User with Practical
     */
    @Test
    public void testManyToManyPracticals() {
        User returnedValue = User.findByName("Bob");
        // Get practicals of the user
        List<Practical> returnedPracticals = returnedValue.getPracticals();

        // Check whether associated users are correct
        assertEquals(returnedPracticals.get(0).getName(), "ProgrammingAssignment");
        assertEquals(returnedPracticals.get(1).getName(), "DocumentingAssignment");
        assertEquals(returnedPracticals.size(), 2);

        returnedValue = User.findByName("Hendrik");
        // Get practicals of the user
        returnedPracticals = returnedValue.getPracticals();

        // Check whether associated users are correct
        assertEquals(returnedPracticals.get(0).getName(), "ProgrammingAssignment");
        assertEquals(returnedPracticals.size(), 1);
    }

    /**
     * Method to test the many-to-many relations of User with PracticalGroups
     */
    @Test
    public void testManyToManyPracticalGroups() {
        User returnedValue = User.findByName("Bob");
        // Get practicalGroups of the user
        List<PracticalGroup> returnedPracticalGroups = returnedValue.getPracticalGroups();

        // Check whether associated users are correct
        assertEquals(returnedPracticalGroups.get(0), bobsGroup);
        assertEquals(returnedPracticalGroups.get(1), bobAndHendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 2);

        returnedValue = User.findByName("Hendrik");
        // Get teams of the user
        returnedPracticalGroups = returnedValue.getPracticalGroups();

        // Check whether associated users are correct
        assertEquals(returnedPracticalGroups.get(0), bobAndHendriksGroup);
        assertEquals(returnedPracticalGroups.size(), 1);
    }

    /**
     * Method to test the one-to-many relation of User and PracticalAdmin
     */
    @Test
    public void testOneToManyPracticalAdmin() {
        User returnedValue = User.findByName("Bob");
        // Get prac of the user
        List<Practical> returnedPracticalsAdmin = returnedValue.getPracticalsAdmin();

        // Check whether associated users are correct
        assertNotNull(returnedPracticalsAdmin);
        assertEquals(returnedPracticalsAdmin.get(0).getName(), "ProgrammingAssignment");
        assertEquals(returnedPracticalsAdmin.get(1).getName(), "DocumentingAssignment");
        assertEquals(returnedPracticalsAdmin.size(), 2);
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
}
