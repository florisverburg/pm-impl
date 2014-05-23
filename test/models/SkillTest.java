package models;

import models.*;
import org.junit.*;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
public class SkillTest extends WithApplication {

    Skill programming;

    /**
     * Setup method for the SkillTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        programming = Skill.findByName("Programming");
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
}
