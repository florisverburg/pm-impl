package controllers;

import com.avaje.ebean.Ebean;
import play.libs.Yaml;
import play.test.WithApplication;

import java.util.List;
import java.util.Map;

/**
 * Created by Marijn Goedegebure on 16-5-2014.
 * Class with methods that can be used by multiple tests
 */
public class TestHelper{

    public static void loadYamlFile(String filename) {
        @SuppressWarnings("unchecked")
        Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml.load(filename);

        // Insert users first
        if(all.get("users") != null)
            Ebean.save(all.get("users"));

        // Insert projects
        if(all.get("practicals") != null) {
            Ebean.save(all.get("practicals"));
            for (Object practical : all.get("practicals")) {
                // Insert the project/user relation
                Ebean.saveManyToManyAssociations(practical, "users");
            }
        }

        // Insert identities
        if(all.get("identities") != null)
            Ebean.save(all.get("identities"));
    }
}
