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

    @Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (User.find.findRowCount() == 0) {
            @SuppressWarnings("unchecked")
            Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml
                    .load("initial-data.yml");

            // Insert users first
            if (all.get("users") != null) {
                Ebean.save(all.get("users"));
            }

            // Insert projects
            if (all.get("practicals") != null) {
                Ebean.save(all.get("practicals"));
                for (Object practical : all.get("practicals")) {
                    // Insert the project/user relation
                    Ebean.saveManyToManyAssociations(practical, "users");
                }
            }

            // Insert identities
            if (all.get("identities") != null) {
                Ebean.save(all.get("identities"));
            }

            // Insert practical groups
            if (all.get("practicalGroups") != null) {
                Ebean.save(all.get("practicalGroups"));
                for (Object practicalGroup : all.get("practicalGroups")) {
                    // Insert the practicalGroup to user relationship
                    Ebean.saveManyToManyAssociations(practicalGroup, "users");
                }
            }
        }
    }
}
