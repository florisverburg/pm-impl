package models;
import models.*;
import org.junit.*;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
public class SkillTest {

    private User bob;
    private User hendrik;

    private Skill programming;
    private Skill documenting;

    /**
     * Setup method for the SkillTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        // Create a new skill
        programming = new Skill("Programming", Skill.Type.PROGRAMMING,10);
        programming.save();

        // Create a new skill
        documenting = new Skill("Documenting", Skill.Type.DOCUMENTING,10);
        documenting.save();

        // Create a new user
        bob = new User("Bob","Verburg","English","bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik","Tienen","Dutch","hendrik@example.com");
        hendrik.save();

        // Add bob to skills
        programming.addUser(bob);
        documenting.addUser(bob);

        // Add hendrik to skill
        programming.addUser(hendrik);
        programming.save();
        documenting.save();
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Check the values of the setUp() method
        assertThat(programming.getName()).isEqualTo("Programming");
        assertThat(programming.getType()).isEqualTo(Skill.Type.PROGRAMMING);
        assertThat(programming.getMaxValue()).isEqualTo(10);
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        programming.setName("Documenting");
        programming.setType(Skill.Type.DOCUMENTING);
        programming.setMaxValue(5);
        programming.save();

        // Check the new values
        assertThat(programming.getName()).isEqualTo("Documenting");
        assertThat(programming.getType()).isEqualTo(Skill.Type.DOCUMENTING);
        assertThat(programming.getMaxValue()).isEqualTo(5);
    }

    /**
     * Method to test the many-to-many relations of Team with User and Right
     */
    @Test
    public void testManyToMany() {
        // Get right
        Skill returnedValue = Skill.findByName("Programming");
        List<User> returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertEquals(returnedUsers.get(1).getFirstName(),"Hendrik");
        assertThat(returnedUsers.size()).isEqualTo(2);

        // Get the other right
        returnedValue = Skill.findByName("Documenting");
        returnedUsers = returnedValue.getUsers();

        // check whether associated teams are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertThat(returnedUsers.size()).isEqualTo(1);
    }
}
