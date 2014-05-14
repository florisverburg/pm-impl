package models;

import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marijn Goedegebure on 14-5-2014.
 * Model class for the database practical
 */
@Entity
public class Practical extends Model {

    /**
     * Primary key of the practical
     */
    @Id
    private long id;

    /**
     * Name of the practical
     */
    @Constraints.Required
    @Constraints.MaxLength(20)
    private String name;

    /**
     * Description of the practical
     */
    @Constraints.Required
    @Constraints.MaxLength(200)
    private String description;

    /**
     * Many-to-many relationship between practical and user
     */
    @ManyToMany(mappedBy = "practicals", cascade = CascadeType.ALL)
    List<User> users = new ArrayList<>();


    /**
     * Finder defined for the practical
     */
    public static Finder<Long, Practical> find =
            new Finder<>(Long.class, Practical.class);


    /**
     * Constructor of the practical
     * @param name of the practical
     * @param description of the practical
     */
    public Practical(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Method to find a practical by its name
     * @param name of the practical to be found
     * @return the found practical
     */
    public static Practical findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

}
