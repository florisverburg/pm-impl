package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Floris on 26/05/14.
 */
public class SkillValueTest extends WithApplication {

    private User user;
    private User user1;
    private Practical practical;
    private Practical practical1;
    private Skill skill;
    private SkillValueUser skillValue1;
    private SkillValuePractical skillValue2;

    /**
     * Setup method for the UserSkillTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        user = User.findByEmail("defaultuser1@example.com");
        user1 = User.findByEmail("admin@example.com");
        practical = Practical.findByName("Programming");
        practical1 = Practical.findByName("Documenting");
        skill = Skill.findByName("Programming");
        skillValue1 = user.getSkillValues().get(0);
        skillValue2 = practical.getSkills().get(0);
    }

    /**
     * Method to info whether the creation of a user skill has been successful
     */
    @Test
    public void createUserSkill() {
        SkillValueUser uSkill = new SkillValueUser(user1, skill, 5);
        uSkill.save();

        // Check the values of the setUp() method
        assertEquals(user.getSkillValues().size(), 2);
        assertEquals(skillValue1.getValue(), new Integer(8));
        assertEquals(skillValue1.getUser(), user);
        assertEquals(skillValue1.getSkill(), skill);

        // Check if creation was ok
        assertEquals(user1.getSkillValues().size(), 1);
        assertEquals(uSkill.getValue(), new Integer(5));
        assertEquals(uSkill.getUser(), user1);
        assertEquals(uSkill.getSkill(), skill);
        assertTrue(uSkill.getId() > 0);
    }

    @Test
    public void createPracticalSkill() {
        SkillValuePractical pSkill = new SkillValuePractical(practical1, skill, 5);
        pSkill.save();

        // Check the values of the setUp() method
        assertEquals(practical.getSkills().size(), 2);
        assertEquals(skillValue2.getValue(), new Integer(7));
        assertEquals(skillValue2.getPractical(), practical);
        assertEquals(skillValue2.getSkill(), skill);

        // Check if creation was ok
        assertEquals(practical1.getSkills().size(), 1);
        assertEquals(pSkill.getValue(), new Integer(5));
        assertEquals(pSkill.getPractical(), practical1);
        assertEquals(pSkill.getSkill(), skill);
        assertTrue(pSkill.getId() > 0);
    }

    /**
     * Method to info the usage of the user skill's setters
     */
    @Test
    public void setters() {
        // Set different values
        skillValue1.setValue(new Integer(5));
        skillValue1.save();

        // Check the new values
        assertEquals(skillValue1.getValue(), new Integer(5));
    }
}
