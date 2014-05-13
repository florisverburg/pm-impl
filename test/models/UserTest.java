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
        bob = new User("Bob", "Verburg", "English", "Bob", "bob@example.com");
        bob.save();
    }

    /**
     * Method to test the many-to-many relationship
     */
    @Test
    public void testManyToMany() {
        Skill programming = new Skill("Programming", Skill.Type.PROGRAMMING, 10);
        Skill documenting = new Skill("Documenting", Skill.Type.DOCUMENTING, 10);
        bob.addSkill(programming);
        bob.addSkill(documenting);
        bob.save();

        bob = User.findByName("Bob");
        List<Skill> bobsSkills = bob.getSkills();
        Skill bobsSkills1 = bobsSkills.get(0);
        Skill bobsSkills2 = bobsSkills.get(1);
        assertThat(bobsSkills1.getName()).isEqualTo("Programming");
        assertThat(bobsSkills2.getName()).isEqualTo("Documenting");
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
