package models;

import org.junit.*;
import play.test.WithApplication;

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
     * Method to info whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Create a new skill
        Skill createdSkill = new Skill("CreatedSkill", Skill.Type.DOCUMENTING);
        createdSkill.save();

        // Retrieve skill from database
        createdSkill = Skill.findByName(createdSkill.getName());

        // Check the values of the setUp() method
        assertEquals(createdSkill.getName(), "CreatedSkill");
        assertEquals(createdSkill.getType(), Skill.Type.DOCUMENTING);
    }

    /**
     * Method to info the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        testSkill.setType(Skill.Type.DOCUMENTING);
        testSkill.save();

        // Check the new values
        assertThat(testSkill.getName()).isEqualTo("Programming");
        assertThat(testSkill.getType()).isEqualTo(Skill.Type.DOCUMENTING);
    }
}
