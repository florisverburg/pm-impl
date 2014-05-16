package models;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
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

    private User bob;
    private User hendrik;


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

        // Create a new user
        bob = new User("Bob","Verburg","bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik","Tienen","hendrik@example.com");
        hendrik.save();

        // Add the rights and users to the admin team
        admin.addRight(read);
        admin.addRight(edit);
        admin.addUser(bob);
        admin.addUser(hendrik);
        admin.save();

        // Add the rights to the user team
        user.addRight(read);
        user.addUser(bob);
        user.save();
    }

    /**
     * Method to test whether the creation of a team has been successful
     */
    @Test
    public void testCreationTeam() {
        // Check the values of the setUp() method for admin
        assertThat(admin.getType()).isEqualTo("admin");
        assertThat(admin.getDescription()).isEqualTo("Admin of the system, has right to everything");
    }

    /**
     * Method to test the usage of the team's setters
     */
    @Test
    public void testSettersTeam() {
        // Set different values
        admin.setType("CEO");
        admin.setDescription("Should be able to do anything he wants");
        admin.save();

        // Check the new values
        assertThat(admin.getType()).isEqualTo("CEO");
        assertThat(admin.getDescription()).isEqualTo("Should be able to do anything he wants");
    }

    /**
     * Method to test the many-to-many relations of Team with Right
     */
    @Test
    public void testManyToManyRights() {
        // Get team
        Team returnedValue = Team.findByName("admin");
        List<Right> returnedRights = returnedValue.getRights();

        // Check whether associated rights are correct
        assertThat(returnedRights.get(0).getType()).isEqualTo("read");
        assertThat(returnedRights.get(1).getType()).isEqualTo("edit");
        assertThat(returnedRights.size()).isEqualTo(2);

        // Get other team
        returnedValue = Team.findByName("user");
        returnedRights = returnedValue.getRights();

        // Check whether associated rights are correct
        assertThat(returnedRights.get(0).getType()).isEqualTo("read");
        assertThat(returnedRights.size()).isEqualTo(1);
    }

    /**
     * Method to test the many-to-many relations of Team with User
     */
    @Test
    public void testManyToManyUsers() {
        Team returnedValue = Team.findByName("admin");
        // Get users of the team
        List<User> returnedUsers = returnedValue.getUsers();

        // Check whether associated users are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertEquals(returnedUsers.get(1).getFirstName(),"Hendrik");
        assertEquals(returnedUsers.size(),2);

        returnedValue = Team.findByName("user");
        // Get users of the team
        returnedUsers = returnedValue.getUsers();

        // Check whether associated users are correct
        assertEquals(returnedUsers.get(0).getFirstName(),"Bob");
        assertEquals(returnedUsers.size(),1);
    }
}
