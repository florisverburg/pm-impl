package helpers;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marijn Goedegebure on 30-5-2014.
 * Class that will be used for the generation of recommendations
 */
public class Recommendation {
    private static final int maxAmountOfRecommendations = 5;

    public static List<PracticalGroup> getRandomRecommendations(Practical practical, User user) {
        List<PracticalGroup> newRecommendations = new ArrayList<PracticalGroup>();
        for (int i = 0; i < maxAmountOfRecommendations; i++) {
            newRecommendations.add(getRandomRecommendation(practical.getPracticalGroups()));
        }
        return newRecommendations;
    }

    public static PracticalGroup getRandomRecommendation(List<PracticalGroup> practicalGroups) {
        Random rn = new Random();
        int generatedNumber = rn.nextInt(practicalGroups.size());
        return practicalGroups.get(generatedNumber);
    }
}
