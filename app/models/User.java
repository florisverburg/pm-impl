package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

/**
 * Created by Freek on 09/05/14.
 * This is the user representation of the database
 */
@Entity
public class User extends Model {

    /**
     * The user identifier
     */
    @Id
    private Long id;
}
