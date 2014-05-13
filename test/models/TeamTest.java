package models;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;

/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
public class TeamTest {
    private Team admin;
    private Team user;

    private Right read;
    private Right edit;


    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        // Create a new team
        admin = new Team("admin", "Admin of the system, has right to everything");
        admin.save();

        // Create a new team for users
        user = new Team("user", "User of the system, can read");
        user.save();

        // Create a new right
        read = new Right("read", "Can read all the webpages");
        read.save();
        // Create a new right
        edit = new Right("edit", "Can edit all the webpages");
        edit.save();

        // Add the rights to the admin team
        admin.addRight(read);
        admin.addRight(edit);
        admin.save();

        // Add the rights to the user team
        user.addRight(read);
        user.save();
    }

    @Test
    public void testFindByNameTeam() {

        Team returnedValue = Team.findByName("admin");
        List<Right> returnedRights = returnedValue.getRights();

        assertThat(returnedRights.get(0).getType()).isEqualTo("read");
        assertThat(returnedRights.get(1).getType()).isEqualTo("edit");
        assertThat(returnedRights.size()).isEqualTo(2);

        returnedValue = Team.findByName("user");
        returnedRights = returnedValue.getRights();

        assertThat(returnedRights.get(0).getType()).isEqualTo("read");
        assertThat(returnedRights.size()).isEqualTo(1);
    }

    @Test
    public void testFindByNameRight() {

        Right returnedValue = Right.findByName("read");
        List<Team> returnedTeams = returnedValue.getTeams();

        assertThat(returnedTeams.get(0).getType()).isEqualTo("admin");
        assertThat(returnedTeams.get(1).getType()).isEqualTo("user");
        assertThat(returnedTeams.size()).isEqualTo(1);

        returnedValue = Right.findByName("edit");
        returnedTeams = returnedValue.getTeams();

        assertThat(returnedTeams.get(0).getType()).isEqualTo("admin");
        assertThat(returnedTeams.size()).isEqualTo(1);
    }
}
