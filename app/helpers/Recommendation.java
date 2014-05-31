package helpers;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.avaje.ebean.Expr.eq;
import static com.avaje.ebean.Expr.or;

/**
 * Created by Marijn Goedegebure on 30-5-2014.
 * Class that will be used for the generation of recommendations
 */
public final class Recommendation {
    private static final int MAXAMOUNTOFRECCOMENDATIONS = 5;

    /**
     * Method to prevent instantiation
     */
    private Recommendation() {
        // Prevent instantiation
        // Optional: throw an exception e.g. AssertionError
        // if this ever *is* called
    }

    /**
     * Method to get random recommendations for the user
     * @param practical to get recommendations for
     * @param user to get recommendations for
     * @return list of maxAmountOfRecommendations recommendations
     */
    public static List<PracticalGroup> getRandomRecommendations(Practical practical, User user) {
        List<PracticalGroup> newRecommendations = new ArrayList<PracticalGroup>();
        for (int i = 0; i < MAXAMOUNTOFRECCOMENDATIONS; i++) {
            newRecommendations.add(getRandomRecommendation(practical.getPracticalGroups()));
        }
        return newRecommendations;
    }

    /**
     * Method to return a single recommendation
     * @param practicalGroups to select from
     * @return random practical group
     */
    public static PracticalGroup getRandomRecommendation(List<PracticalGroup> practicalGroups) {
        Random rn = new Random();
        int generatedNumber = rn.nextInt(practicalGroups.size());
        return practicalGroups.get(generatedNumber);
    }

    /**
     * Method to get non random generated recommendations
     * @param practical to find recommendations for
     * @param user to find recommendations for
     * @return list of recommendations
     */
    public static List<PracticalGroup> getRecommendations(Practical practical, User user) {
        PracticalGroup practicalGroupOfUser = PracticalGroup.findWithPracticalAndUser(practical, user);
        List<PracticalGroup> newRecommendations = comparePracticalGroupWithPractical(
                practicalGroupOfUser, practical.getPracticalGroups());
        return newRecommendations;
    }

    /**
     * Method to compare the different practical groups of the practical with the practical group of the user
     * @param practicalGroupOfUser to compare
     * @param practicalGroups to compare to
     * @return list of recommendations
     */
    public static List<PracticalGroup> comparePracticalGroupWithPractical(
            PracticalGroup practicalGroupOfUser, List<PracticalGroup> practicalGroups) {
        List<PracticalGroup> newRecommendations = new ArrayList<PracticalGroup>();
        List<Integer> currentScores = new ArrayList<Integer>();
        for(PracticalGroup practicalGroup : practicalGroups) {
            if (!practicalGroupOfUser.equals(practicalGroup)) {
                int difference = calculateDifferenceAverageAndGiven(practicalGroupOfUser, practicalGroup);
                // get index where the number should be inserted into the list
                int index = getIndexForAddition(currentScores, difference);
                if (index != -1) {
                    // Add the difference to the appropriate list
                    currentScores = addIntegerToListGivenIndex(currentScores, index, difference);
                    // Add the practical group to the appropriate list
                    newRecommendations = addPracticalGroupToListGivenIndex(newRecommendations, index, practicalGroup);
                }
            }
        }
        return newRecommendations;
    }

    /**
     * Method that compares the practical group of the user
     * and another practical group and calculates a difference between these
     * and a given list of values by the practical
     * @param practicalGroupOfUser to compare
     * @param practicalGroupToCompare to compare with
     * @return difference between the combined practical groups and the given list
     */
    public static int calculateDifferenceAverageAndGiven(
            PracticalGroup practicalGroupOfUser, PracticalGroup practicalGroupToCompare) {
        List<Integer> listOfAverages = calculateAverageForGroupAndGroupUser(
                practicalGroupOfUser, practicalGroupToCompare);
        List<Integer> listOfPractical = new ArrayList<Integer>();
        listOfPractical.add(1);
        listOfPractical.add(1);
        int difference = 0;
        for (int i = 0; i < listOfAverages.size(); i++) {
            difference += listOfPractical.get(i) - listOfAverages.get(i);
        }
        return difference;
    }

    /**
     * Method to calculate the average of the user's practical group and another practical group
     * @param practicalGroupOfUser to calculate the average of
     * @param practicalGroupToCompare to calculate the average of
     * @return list of integers with averages
     */
    public static List<Integer> calculateAverageForGroupAndGroupUser(
            PracticalGroup practicalGroupOfUser, PracticalGroup practicalGroupToCompare) {
        List<Skill> skills = Skill.findAll();
        List<Integer> listOfAverages = new ArrayList<Integer>();
        for(int i = 0; i < skills.size(); i++) {
            listOfAverages.add(calculateAverageForSkill(skills.get(i), practicalGroupOfUser, practicalGroupToCompare));
        }
        return listOfAverages;
    }

    /**
     * Method to calculate the average of a practicalgroup given a skill
     * @param skill that is given
     * @param practicalGroupOfUser to calculate the average of
     * @param practicalGroupToCompare to calculate the average of
     * @return list of averages
     */
    public static int calculateAverageForSkill(
            Skill skill, PracticalGroup practicalGroupOfUser, PracticalGroup practicalGroupToCompare) {
        List<UserSkill> userSkillsPracticalGroup = UserSkill.find.where().and(
                eq("skill.name", skill.getName()),
                or(
                        eq("user.practicalGroups.id", practicalGroupOfUser.getId()),
                        eq("user.practicalGroups.id", practicalGroupToCompare.getId())
                )
        ).findList();
        int average = 0;
        for(UserSkill userSkill : userSkillsPracticalGroup) {
            // calculate average
            average += userSkill.getValue();
        }
        average = average / userSkillsPracticalGroup.size();
        return average;
    }

    /**
     * Method to add a difference on a specific index, the rest of the values are moved up one spot
     * @param list to add to
     * @param index to add on
     * @param item to add
     * @return list where an addition has been made
     */
    public static List<Integer> addIntegerToListGivenIndex(List<Integer> list, int index, int item) {
        for(int i = index; i < MAXAMOUNTOFRECCOMENDATIONS-1 && i < list.size(); i++) {
            list.set(i+1, list.get(i));
        }
        list.set(index, item);
        return list;
    }

    /**
     * Method to add a practical group on a specific index, the rest of the values are moved up one spot
     * @param list to add to
     * @param index to add on
     * @param item to add
     * @return list where an addition has been made
     */
    public static List<PracticalGroup> addPracticalGroupToListGivenIndex(
            List<PracticalGroup> list, int index, PracticalGroup item) {
        for(int i = index; i < MAXAMOUNTOFRECCOMENDATIONS-1 && i < list.size(); i++) {
            list.set(i+1, list.get(i));
        }
        list.set(index, item);
        return list;
    }

    /**
     * Method to determine on which index the new practical group and difference should be added
     * @param list to add to
     * @param number to add
     * @return index where to add
     */
    public static int getIndexForAddition(List<Integer> list, int number) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) == null) {
                return i;
            }
            if(list.get(i) < number) {
                return i;
            }
        }
        return -1;
    }
}
