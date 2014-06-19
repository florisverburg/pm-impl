package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Freek on 19/06/14.
 */
public class RecommendationTest extends WithApplication {

    Practical practical;
    User user1;
    User user2;
    Skill skill1;
    Skill skill2;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Get the stuff from the initial database
        practical = Practical.findByName("Programming");
        user1 = User.findByEmail("defaultuser1@example.com");
        user2 = User.findByEmail("defaultuser1@example.com");
        skill1 = Skill.findByName("Programming");
        skill2 = Skill.findByName("Documenting");
    }

    @Test
    public void calculateAverage() {
        PracticalGroup group1 = PracticalGroup.findWithPracticalAndUser(practical, user1);
        PracticalGroup group2 = PracticalGroup.findWithPracticalAndUser(practical, user2);

        Map<Skill, Double> average = Recommendation.average(group1, group2);

        assertEquals(average.get(skill1), 8.0, 0);
        assertEquals(average.get(skill2), 7.0, 0);
    }

    @Test
    public void calculateDistance() {
        PracticalGroup group1 = PracticalGroup.findWithPracticalAndUser(practical, user1);
        PracticalGroup group2 = PracticalGroup.findWithPracticalAndUser(practical, user2);

        HashMap<Skill, Double> average = Recommendation.average(group1, group2);
        Double distance = Recommendation.distance(average, SkillValuePractical.findByPractical(practical));

        HashMap<Skill, Double> average2 = Recommendation.average(group2, group1);
        Double distance2 = Recommendation.distance(average2, SkillValuePractical.findByPractical(practical));

        assertEquals(distance, 4.12310562, 0.00000001);
        assertEquals(distance, distance2);
    }

    @Test
    public void recommend() {
        List<PracticalGroup> groups = Recommendation.recommend(user1, practical, 1);
        PracticalGroup myGroup = PracticalGroup.findWithPracticalAndUser(practical, user1);

        assertFalse(groups.isEmpty());

        Double maxDistance = Double.MIN_VALUE;
        for(PracticalGroup group: groups) {
            HashMap<Skill, Double> average = Recommendation.average(group, myGroup);
            Double distance = Recommendation.distance(average, SkillValuePractical.findByPractical(practical));

            assertThat(distance).isGreaterThanOrEqualTo(maxDistance);
            maxDistance = distance;
        }
    }
}
