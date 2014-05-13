package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import java.util.List;

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
        // Create a new user
        bob = new User("Bob","Verburg","English","bob@example.com");
        bob.save();

        // Create skills
        Skill programming = new Skill("Programming", Skill.Type.PROGRAMMING, 10);
        Skill documenting = new Skill("Documenting", Skill.Type.DOCUMENTING, 10);

        // Add skills to user bob
        bob.addSkill(programming);
        bob.addSkill(documenting);
        bob.save();
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
     * Method to test the many-to-many relationship of User and Skills
     */
    @Test
    public void testManyToMany() {
        bob = User.findByName("Bob");
        List<Skill> bobsSkills = bob.getSkills();
        assertThat(bobsSkills.get(0).getName()).isEqualTo("Programming");
        assertThat(bobsSkills.get(1).getName()).isEqualTo("Documenting");
        assertThat(bobsSkills.size()).isEqualTo(2);
    }


    @Test
    public void authenticateSucces() {
        new PasswordIdentity(floortje, "floor@floor.com", "florina").save();

        assertNotNull(User.authenticate("floor@floor.com", "florina"));
        assertEquals(User.authenticate("floor@floor.com", "florina"), floortje);
    }

    @Test
    public void authenticateFail() {
        User floortje = new User("floortje");
        floortje.save();
        new PasswordIdentity(floortje, "floor@floor.com", "florina").save();

        assertNull(User.authenticate("floor@wrongemail.com", "florina"));
        assertNull(User.authenticate("floor@floor.com", "wrongpass"));
    }
}
