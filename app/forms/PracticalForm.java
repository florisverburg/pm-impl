package forms;

import models.*;
import play.data.validation.*;

/**
 * Created by Freek on 26/05/14.
 * The basic practical form for the teachers
 */
public class PracticalForm {

    /**
     * The Name.
     */
    @Constraints.Required
    String name;

    /**
     * The Description.
     */
    @Constraints.Required
    String description;

    /**
     * Create a new empty practical form
     */
    public PracticalForm() {

    }

    /**
     * Create a new practical form based on a practical
     * @param practical The practical
     */
    public PracticalForm(Practical practical) {
        this.name = practical.getName();
        this.description = practical.getDescription();
    }

    /**
     * Gets name.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description.
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Save the the practical from the form
     * @param user The user which is the admin of the practical
     * @return The created practical
     */
    public Practical save(User user) {
        Practical practical = new Practical(this.name, this.description);
        practical.setAdmin(user);
        practical.addUsers(user);
        practical.save();

        return practical;
    }
}
