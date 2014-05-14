package models;

import models.*;
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

    private Team admin;
    private Team user;
    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        // Create a new user
        bob = new User("Bob","Verburg","English","bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik","Tienen","Dutch","hendrik@example.com");
        hendrik.save();

        // Create a new skill
        programming = new Skill("Programming", Skill.Type.PROGRAMMING,10);
        programming.save();

        // Create a new skill
        documenting = new Skill("Documenting", Skill.Type.DOCUMENTING,10);
        documenting.save();

        // Create a new team
        admin = new Team("admin", "Admin of the system, has right to everything");
        admin.save();

        // Create a new team for users
        user = new Team("user", "User of the system, can read");
        user.save();

        // Add skills to user bob
        bob.addSkill(programming);
        bob.addSkill(documenting);
        // Add teams to user bob
        bob.addTeam(admin);
        bob.addTeam(user);
        bob.save();

        // Add skills to user hendrik
        hendrik.addSkill(programming);
        // Add teams to user hendrik
        hendrik.addTeam(user);
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
        assertThat(bob.getLanguage()).isEqualTo("English");
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
        bob.setLanguage("Dutch");
        bob.setEmail("erica@example.com");
        bob.save();

        // Check the new values
        assertThat(bob.getFirstName()).isEqualTo("Erica");
        assertThat(bob.getLastName()).isEqualTo("Tienen");
        assertThat(bob.getLanguage()).isEqualTo("Dutch");
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
     * Method to test the many-to-many relations of User with Team
     */
    @Test
    public void testManyToManyTeams() {
        User returnedValue = User.findByName("Bob");
        // Get teams of the user
        List<Team> returnedTeams = returnedValue.getTeams();

        // Check whether associated users are correct
        assertEquals(returnedTeams.get(0).getType(),"admin");
        assertEquals(returnedTeams.get(1).getType(),"user");
        assertEquals(returnedTeams.size(),2);

        returnedValue = User.findByName("Hendrik");
        // Get teams of the user
        returnedTeams = returnedValue.getTeams();

        // Check whether associated users are correct
        assertEquals(returnedTeams.get(0).getType(),"user");
        assertEquals(returnedTeams.size(),1);
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
