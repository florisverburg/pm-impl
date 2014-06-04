package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Floris on 26/05/14.
 */
public class UserSkillTest extends WithApplication {

    private User user;
    private Skill skill;
    private List<SkillValueUser> skillValues;
    private SkillValueUser skillValue1;

    /**
     * Setup method for the UserSkillTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        user = User.findByEmail("defaultuser1@example.com");
        skill = Skill.findByName("Programming");
        skillValues = user.getSkillValues();
        skillValue1 = skillValues.get(0);
    }

    /**
     * Method to test whether the creation of a user skill has been successful
     */
    @Test
    public void testCreationUserSkill() {
        // Check the values of the setUp() method
        assertEquals(skillValues.size(), 2);
        assertEquals(skillValue1.getValue(), new Integer(8));
        assertEquals(skillValue1.getUser(), user);
        assertEquals(skillValue1.getSkill(), skill);
    }

    /**
     * Method to test the usage of the user skill's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        skillValue1.setValue(new Integer(5));
        skillValue1.save();

        // Check the new values
        assertEquals(skillValue1.getValue(), new Integer(5));
    }
}
