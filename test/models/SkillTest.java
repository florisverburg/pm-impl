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

    Skill testSkill;

    /**
     * Setup method for the SkillTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testSkill = Skill.findByName("Programming");
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Create a new skill
        Skill createdSkill = new Skill("CreatedSkill", Skill.Type.DOCUMENTING, 15);
        createdSkill.save();

        // Retrieve skill from database
        createdSkill = Skill.findById(createdSkill.getId());

        // Check the values of the setUp() method
        assertEquals(createdSkill.getName(), "CreatedSkill");
        assertEquals(createdSkill.getType(), Skill.Type.DOCUMENTING);
        assertEquals((int)createdSkill.getMaxValue(), 15);
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        testSkill.setName("Documenting");
        testSkill.setType(Skill.Type.DOCUMENTING);
        testSkill.setMaxValue(5);
        testSkill.save();

        // Check the new values
        assertThat(testSkill.getName()).isEqualTo("Documenting");
        assertThat(testSkill.getType()).isEqualTo(Skill.Type.DOCUMENTING);
        assertThat(testSkill.getMaxValue()).isEqualTo(5);
    }
}
