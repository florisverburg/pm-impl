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

    private User bob;

    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        bob = User.findByName("Bob");
    }

    /**
     * Method to test whether the creation of an user has been successful
     */
    @Test
    public void testCreationUser() {
        // Create a new user
        User createdUser = new User ("Laura", "Dragos", "laura@example.com", User.Type.User);
        createdUser.save();

        // Retrieve user from database
        User retrievedUser = User.findById(createdUser.getId());

        // Check the values of the setUp() method
        assertEquals(retrievedUser.getFirstName(), "Laura");
        assertEquals(retrievedUser.getLastName(), "Dragos");
        assertEquals(retrievedUser.getEmail(), "laura@example.com");
        assertEquals(retrievedUser.getType(), User.Type.User);
        assertEquals(retrievedUser.getToken().getClass(), String.class);
    }

    /**
     * Method to test the usage of the user's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        bob.setFirstName("Erica");
        bob.setLastName("Tienen");
        bob.setEmail("erica@example.com");
        bob.setType(User.Type.Admin);
        bob.setToken("abc");
        bob.save();

        // Retrieve bob from the database
        User retrievedUser = User.findById(bob.getId());

        // Check the new values
        assertEquals(retrievedUser.getFirstName(), "Erica");
        assertEquals(retrievedUser.getLastName(), "Tienen");
        assertEquals(retrievedUser.getEmail(), "erica@example.com");
        assertEquals(retrievedUser.getType(), User.Type.Admin);
        assertEquals(retrievedUser.getToken(), "abc");
    }

    /**
     * Test getByEmailMethod
     */
    @Test
    public void getByEmail() {
        // Test whether the findByEmail returns the correct values
        User createRob = new User("rob", "lastName", "rob@example.com", User.Type.Admin);
        createRob.save();
        User rob = User.findByEmail("rob@none.com");
        // Check the new values
        assertEquals(rob.getFirstName(), "rob");
        assertEquals(rob.getLastName(), "Tienen");
        assertEquals(rob.getEmail(), "rob@example.com");
        assertEquals(rob.getType(), User.Type.Admin);


        // retrieve the user rob by his email and by his id
        User retrievedUserById = User.findById(rob.getId());
        User retrievedUserByEmail = User.findByEmail(rob.getEmail());

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
        User rob = new User("rob", "lastName", "rob@example.com", User.Type.User);
        rob.save();
        // Get skills
        Skill programming = Skill.findByName("Programming");
        Skill documenting = Skill.findByName("Documenting");

        User retrievedUser = User.findById(rob.getId());
        // Check values
        assertEquals(retrievedUser.getSkills().size(), 0);

        // Add skill to user
        retrievedUser.addSkill(programming);
        retrievedUser.save();

        retrievedUser = User.findById(rob.getId());
        // Check values
        assertEquals(retrievedUser.getSkills().size(), 1);
        assertEquals(retrievedUser.getSkills().get(0).getName(), programming.getName());
    }

    /**
     *
     */

    @Test
    public void authenticateSuccess() {
        new PasswordIdentity(bob, bob.getEmail(), "florina").save();

        assertNotNull(User.authenticate(bob.getEmail(), "florina"));
        assertEquals(User.authenticate(bob.getEmail(), "florina"), bob);
    }

    @Test
    public void authenticateFail() {
        new PasswordIdentity(bob, bob.getEmail(), "florina").save();

        assertNull(User.authenticate("floor@wrongemail.com", "florina"));
        assertNull(User.authenticate(bob.getEmail(), "wrongpass"));
    }

    @Test
     public void typeTest() {
        assertNotEquals(bob.getType(), User.Type.Teacher);
        assertEquals(bob.getType(), User.Type.User);
    }


    /**
     * This test might needs to be in the InviteTest class
     */
    @Test
    public void findPendingInvitesTest() {
        // Create a new user rob
        User rob = new User("rob", "lastName", "rob@example.com", User.Type.Admin);
        Practical practical = Practical.findByName("Programming");
        rob.addPractical(practical);
        rob.save();
        User hendrik = User.findByName("Hendrik");
        User peter = User.findByName("Peter");
        // Send invites
        Invite inviteRobToBob = Invite.sendInvite(practical, rob, bob);
        Invite inviteHendrikToRob = Invite.sendInvite(practical, hendrik, rob);
        Invite invitePeterToRob = Invite.sendInvite(practical, peter, rob);

        List<Invite> foundInvites = rob.findPendingInvitesUser(practical);

        // Check whether the values returned are correct
        assertEquals(foundInvites.size(), 3);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }

        // Reverse the invitation
        rob.getInvitesSend().remove(inviteRobToBob);
        rob.save();
        Invite.sendInvite(practical, bob, rob);

        // Check whether the values returned did not change
        // Check whether the values returned are correct
        assertEquals(foundInvites.size(), 3);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }

        // Change the state of the invites
        inviteRobToBob.setState(Invite.State.Accepted);
        inviteRobToBob.save();

        inviteHendrikToRob.setState(Invite.State.Rejected);
        inviteHendrikToRob.save();

        invitePeterToRob.setState(Invite.State.Withdrawn);
        invitePeterToRob.save();

        foundInvites = rob.findPendingInvitesUser(practical);

        // Check whether the values returned are change appropriately
        assertEquals(foundInvites.size(), 0);
        for (Invite invite : foundInvites) {
            assertEquals(invite.getState(), Invite.State.Pending);
        }
    }
}
