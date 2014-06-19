package models;

import java.util.*;

import static com.avaje.ebean.Expr.eq;
import static com.avaje.ebean.Expr.in;
import static com.avaje.ebean.Expr.or;

/**
 * Created by Marijn Goedegebure on 30-5-2014.
 * Class that will be used for the generation of recommendations
 */
public final class Recommendation {

    /**
     * The amount of recommendations per page
     */
    private static final int PAGE_SIZE = 5;

    /**
     * Method to prevent instantiation
     */
    private Recommendation() {
        // Prevent instantiation
        // Optional: throw an exception e.g. AssertionError
        // if this ever *is* called
    }

    /**
     * Calculates the new average in skills between 2 practical groups when they are combined
     * @param practicalGroup1 the first practical group to combine
     * @param practicalGroup2 the second practical group to combine
     * @return the new calculated average in skills of the group
     */
    public static HashMap<Skill, Double> average(PracticalGroup practicalGroup1, PracticalGroup practicalGroup2) {
        HashMap<Skill, Double> result = new HashMap<Skill, Double>();

        // Go through all the skills
        for(Skill skill : Skill.findAll()) {
            List<User> users = practicalGroup1.getGroupMembers();
            users.addAll(practicalGroup2.getGroupMembers());
            List<SkillValue> uSkills = SkillValue.find.where().and(
                    eq("skill.name", skill.getName()),
                    in("user", users)
            ).findList();

            // Calculate the average here through support of ebean is limited (maybe later fix RAW SQL)
            double skillAverage = 0;
            for(SkillValue skillValue: uSkills) {
                skillAverage += skillValue.getValue();
            }

            if(skillAverage > 0 && uSkills.size() > 0) {
                skillAverage /= uSkills.size();
            }

            // Put the result in the HashMap
            result.put(skill, skillAverage);
        }

        return result;
    }

    /**
     * Calculates the distance between 2 skill sets
     * @param skills The practical groups' skills
     * @param pSkills The practical required skills
     * @return The distance between the practical groups' skills and the practical skills
     */
    public static double distance(HashMap<Skill, Double> skills, List<SkillValuePractical> pSkills) {
        double average = 0;

        // Go trough all the practicum skills
        for(SkillValue pSkill : pSkills) {
            average += Math.pow(pSkill.getValue() - skills.get(pSkill.getSkill()), 2);
        }

        // Square the average over all the different skills
        if(pSkills.size() > 0) {
            average = Math.pow(average, 1 / (double) pSkills.size());
        }

        return average;
    }

    /**
     * Recommend practical groups to a certain user
     * @param user The user that wants the recommendation
     * @param practical The practical the user want the recommendation for
     * @param page The page of the recommendation
     * @return Recommendations based on the skills of the practical group
     */
    public static List<PracticalGroup> recommend(User user, Practical practical, int page) {
        HashMap<PracticalGroup, Double> list = new HashMap<PracticalGroup, Double>();
        PracticalGroup practicalGroup1 = PracticalGroup.findWithPracticalAndUser(practical, user);

        // Go trough all the practical groups
        for(PracticalGroup practicalGroup2 : PracticalGroup.findByPractical(practical)) {
            // Check if it is not our own group
            if(practicalGroup1.equals(practicalGroup2)) {
                continue;
            }

            // Calculate average and then the distance
            HashMap<Skill, Double> average = average(practicalGroup1, practicalGroup2);
            list.put(practicalGroup2, distance(average, SkillValuePractical.findByPractical(practical)));
        }

        // Sort the HashMap by values
        LinkedList sortedList = sortByValues(list);


        return sortedList.subList(
                Math.min((page - 1) * PAGE_SIZE, sortedList.size()),
                Math.min(page * PAGE_SIZE, sortedList.size()));
    }

    /**
     * Sort a HashMap by it's values
     * @param map The HashMap to sort
     * @return A sorted LinkedList containing the
     */
    private static LinkedList<PracticalGroup> sortByValues(final HashMap<PracticalGroup, Double> map) {
        LinkedList<PracticalGroup> list = new LinkedList<PracticalGroup>(map.keySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return map.get(o1)
                        .compareTo(map.get(o2));
            }
        });

        return list;
    }
}
