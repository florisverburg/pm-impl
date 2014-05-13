import com.avaje.ebean.Ebean;
import models.*;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;

/**
 * Created by Freek on 13/05/14.
 */
public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (User.find.findRowCount() == 0) {
            Ebean.save((List) Yaml.load("initial-data.yml"));
        }
    }
}
