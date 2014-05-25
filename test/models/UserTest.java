package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.*;


/**
 * Simple test that tests the User model
 */
public class UserTest extends WithApplication {

    private User testUser1;

    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testUser1 = User.findByName("DefaultUser1");
    }

    /**
     * Method to test whether the creation of an user has been successful
     */
    @Test
    public void testCreationUser() {
        // Create a new user
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.Admin);
        createdUser.save();

        // Retrieve user from database
        User retrievedUser = User.findById(createdUser.getId());

        // Check the values of the setUp() method
        // Check the new values
        assertEquals(createdUser.getFirstName(), "CreatedUser");
        assertEquals(createdUser.getLastName(), "LastName");
        assertEquals(createdUser.getEmail(), "createduser@example.com");
        assertEquals(createdUser.getType(), User.Type.Admin);
        assertEquals(retrievedUser.getToken().getClass(), String.class);
    }

    /**
     * Method to test the usage of the user's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        testUser1.setFirstName("SetUser");
        testUser1.setLastName("SetUser");
        testUser1.setEmail("setuser@example.com");
        testUser1.setType(User.Type.Admin);
        testUser1.setToken("newToken");
        testUser1.save();

        // Retrieve testUser1 from the database
        User retrievedUser = User.findById(testUser1.getId());

        // Check the new values
        assertEquals(retrievedUser.getFirstName(), "SetUser");
        assertEquals(retrievedUser.getLastName(), "SetUser");
        assertEquals(retrievedUser.getEmail(), "setuser@example.com");
        assertEquals(retrievedUser.getType(), User.Type.Admin);
        assertEquals(retrievedUser.getToken(), "newToken");
    }

    /**
     * Test getByEmailMethod
     */
    @Test
    public void findByEmail() {
        // Test whether the findByEmail returns the correct values
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.Admin);
        createdUser.save();
        createdUser = User.findByEmail(createdUser.getEmail());
        // Check the new values
        assertEquals(createdUser.getFirstName(), "CreatedUser");
        assertEquals(createdUser.getLastName(), "LastName");
        assertEquals(createdUser.getEmail(), "createduser@example.com");
        assertEquals(createdUser.getType(), User.Type.Admin);


        // retrieve the user createdUser by his email and by his id
        User retrievedUserById = User.findById(createdUser.getId());
        User retrievedUserByEmail = User.findByEmail(createdUser.getEmail());

        // Test whether the two functions return the same data
        assertEquals(retrievedUserById, retrievedUserByEmail);
        assertEquals(retrievedUserById.getId(), retrievedUserByEmail.getId());
        assertEquals(retrievedUserById.getEmail(), retrievedUserByEmail.getEmail());
    }

    /**
     * Skill many-to-many relationship test
     */
    @Test
    public void testSkillManyToManyRelationship() {
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.User);
        createdUser.save();
        // Get skills
        Skill programming = Skill.findByName("Programming");
        Skill documenting = Skill.findByName("Documenting");

        User retrievedUser = User.findById(createdUser.getId());
        // Check values
        assertEquals(retrievedUser.getSkills().size(), 0);

        // Add skill to user
        retrievedUser.addSkill(programming);
        retrievedUser.save();

        retrievedUser = User.findById(createdUser.getId());
        // Check values
        assertEquals(retrievedUser.getSkills().size(), 1);
        assertEquals(retrievedUser.getSkills().get(0).getName(), programming.getName());
    }

    /**
     *
     */

    @Test
    public void authenticateSuccess() {
        new PasswordIdentity(testUser1, testUser1.getEmail(), "florina").save();

        assertNotNull(User.authenticate(testUser1.getEmail(), "florina"));
        assertEquals(User.authenticate(testUser1.getEmail(), "florina"), testUser1);
    }

    @Test
    public void authenticateFail() {
        new PasswordIdentity(testUser1, testUser1.getEmail(), "florina").save();

        assertNull(User.authenticate("floor@wrongemail.com", "florina"));
        assertNull(User.authenticate(testUser1.getEmail(), "wrongpass"));
    }

    @Test
     public void typeTest() {
        assertNotEquals(testUser1.getType(), User.Type.Teacher);
        assertEquals(testUser1.getType(), User.Type.User);
    }


    /**
     * This test might needs to be in the InviteTest class
     */
    @Test
    public void findPendingInvitesTest() {
        // Create a new user createdUser
        User createdUser = new User("CreatedUser", "lastName", "createduser@example.com", User.Type.Admin);
        Practical practical = Practical.findByName("Programming");
        createdUser.addPractical(practical);
        createdUser.save();
        User defaultUser3 = User.findByName("DefaultUser3");
        User defaultUser2 = User.findByName("DefaultUser2");
        // Send invites
        Invite inviteCreatedUserToTestUser1 = Invite.sendInvite(practical, createdUser, testUser1);
        Invite inviteDefaultUser3ToCreatedUser = Invite.sendInvite(practical, defaultUser3, createdUser);
        Invite inviteDefaultUser2ToCreatedUser = Invite.sendInvite(practical, defaultUser2, createdUser);

        List<Invite> foundInvites = createdUser.findPendingInvitesUser(practical);

        // Check whether the values returned are correct
        assertEquals(foundInvites.size(), 3);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }

        // Reverse the invitation
        createdUser.getInvitesSend().remove(inviteCreatedUserToTestUser1);
        createdUser.save();
        Invite.sendInvite(practical, testUser1, createdUser);

        // Check whether the values returned did not change
        // Check whether the values returned are correct
        assertEquals(foundInvites.size(), 3);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }

        // Change the state of the invites
        inviteCreatedUserToTestUser1.setState(Invite.State.Accepted);
        inviteCreatedUserToTestUser1.save();

        inviteDefaultUser3ToCreatedUser.setState(Invite.State.Rejected);
        inviteDefaultUser3ToCreatedUser.save();

        inviteDefaultUser2ToCreatedUser.setState(Invite.State.Withdrawn);
        inviteDefaultUser2ToCreatedUser.save();

        foundInvites = createdUser.findPendingInvitesUser(practical);

        // Check whether the values returned are change appropriately
        assertEquals(foundInvites.size(), 0);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }
    }
}
