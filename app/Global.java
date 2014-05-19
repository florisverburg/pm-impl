import com.avaje.ebean.Ebean;
import models.*;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

/**
 * Created by Freek on 13/05/14.
 * Handles global settings, it overrides methods for onstart and onstop functionality
 */
public class Global extends GlobalSettings {

    /**
     * Insert objects into the database
     * @param objects The objects to insert
     */
    private void insertObjects(List<Object> objects) {
        if(objects != null) {
            Ebean.save(objects);
        }
    }

    /**
     * Insert Many to Many relation into the database
     * @param objects The objects with the Many to Many relation
     * @param key The key used in the Many to Many relation
     */
    private void insertManyToMany(List<Object> objects, String key) {
        if(objects != null) {
            for(Object obj : objects) {
                Ebean.saveManyToManyAssociations(obj, key);
            }
        }
    }

    /**
     * When the application starts and the database isn't populated, populate the database
     * @param app The application that is started
     */
    @Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (User.find.findRowCount() == 0) {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml
                    .load("initial-data.yml");

            // Insert users
            insertObjects(all.get("users"));

            // Insert projects
            insertObjects(all.get("practicals"));
            insertManyToMany(all.get("practicals"), "users");

            // Insert identities
            insertObjects(all.get("identities"));

            // Insert practical groups
            insertObjects(all.get("practicalGroups"));
            insertManyToMany(all.get("practicalGroups"), "users");
        }
    }
}
