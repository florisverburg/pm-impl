package models;
import junit.framework.*;
import junit.framework.Assert;
import models.*;
import org.junit.*;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by Marijn Goedegebure on 13-5-2014.
 */
public class RightTest {

    private Right read;
    private Right edit;

    private Team admin;
    private Team user;

    /**
     * Setup method for the RightTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        // Create a new right
        read = new Right("read", "Can read all the webpages");
        read.save();
        // Create a new right
        edit = new Right("edit", "Can edit all the webpages");
        edit.save();

        // Create a new team
        admin = new Team("admin", "Admin of the system, has right to everything");
        admin.save();

        // Create a new team for users
        user = new Team("user", "User of the system, can read");
        user.save();

        // Add the rights to the admin team
        admin.addRight(read);
        admin.addRight(edit);
        admin.save();

        // Add the rights to the user team
        user.addRight(read);
        user.save();
    }

    /**
     * Method to test whether the creation of a right has been successful
     */
    @Test
    public void testCreationSkill() {
        // Check the values of the setUp() method
        assertEquals(read.getType(),"read");
        assertEquals(read.getDescription(),"Can read all the webpages");
    }

    /**
     * Method to test the usage of the right's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        read.setType("test");
        read.setDescription("test can see what happens");
        read.save();

        // Check the new values
        assertEquals(read.getType(),"test");
        assertEquals(read.getDescription(), "test can see what happens");
    }

    /**
     * Method to test the many-to-many relations of Right with Team
     */
    @Test
    public void testManyToMany() {
        // Get right
        Right returnedValue = Right.findByName("read");
        List<Team> returnedTeams = returnedValue.getTeams();

        // check whether associated teams are correct
        assertThat(returnedTeams.get(0).getType()).isEqualTo("admin");
        assertThat(returnedTeams.get(1).getType()).isEqualTo("user");
        assertThat(returnedTeams.size()).isEqualTo(2);

        // Get the other right
        returnedValue = Right.findByName("edit");
        returnedTeams = returnedValue.getTeams();

        // check whether associated teams are correct
        assertThat(returnedTeams.get(0).getType()).isEqualTo("admin");
        assertThat(returnedTeams.size()).isEqualTo(1);
    }
}
