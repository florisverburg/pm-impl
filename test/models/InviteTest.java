package models;

import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class to test the model representation of the database table of invite
 */
public class InviteTest extends WithApplication {

    private User testUser1;
    private User testUser2;

    private Practical testPractical;

    private Invite testInvite1;
    private Invite testInvite2;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testUser1 = User.findByName("DefaultUser1");
        testUser2 = User.findByName("DefaultUser2");
        testPractical = Practical.findByName("Programming");
        List<Invite> listOfPendingInvites = Invite.findPendingInvitesWhereUser(testUser1, testPractical);
        testInvite1 = listOfPendingInvites.get(0);
        testInvite2 = listOfPendingInvites.get(1);
    }

    @Test
    public void testCreationInvite() {
        User sender = testUser1;
        User receiver = testUser2;

        Invite newInvite = new Invite(testPractical, sender, receiver);
        newInvite.save();

        Invite createdInvite = Invite.findById(newInvite.getId());

        // Test the values set in the before method
        assertEquals(createdInvite.getPractical().getId(), testPractical.getId());
        assertEquals(createdInvite.getSender().getId(), sender.getId());
        assertEquals(createdInvite.getReceiver().getId(), receiver.getId());
        assertEquals(createdInvite.getState(), newInvite.getState());
    }

    @Test
    public void testSetters() {
        // new values to set
        Practical newSetPractical = Practical.findByName("Documenting");

        // Set new values
        testInvite1.setPractical(newSetPractical);
        testInvite1.setSender(testUser1);
        testInvite1.setReceiver(testUser2);
        testInvite1.setState(Invite.State.Accepted);
        testInvite1.save();

        // Get new instance
        Invite setTestInvite = Invite.findById(testInvite1.getId());

        // Test new values
        assertEquals(setTestInvite.getPractical().getId(), newSetPractical.getId());
        assertEquals(setTestInvite.getSender().getId(), testUser1.getId());
        assertEquals(setTestInvite.getReceiver().getId(), testUser2.getId());
        assertEquals(setTestInvite.getState(), Invite.State.Accepted);
    }

    /**
     * Test for the method findPendingInvitesWhereUser
     */
    @Test
    public void testFindPendingInvitesWhereUser() {
        // Create Users that will be used in the creation of the invites
        // Also create a user that has 0 pending invites as a start (a new user)
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.User);
        User senderInvite1 = createdUser;
        User receiverInvite1 = testUser2;
        User senderInvite2 = User.findByName("DefaultUser4");
        User receiverInvite2 = createdUser;

        // Check if the created user has not received or send any invites yet
        assertEquals(createdUser.getInvitesReceived().size(), 0);
        assertEquals(createdUser.getInvitesSend().size(), 0);
        // Check if the method we are testing also returns the appropriate amount of invites
        assertEquals(Invite.findPendingInvitesWhereUser(createdUser, testPractical).size(), 0);

        // Create invites
        Invite newCreatedInvite1 = new Invite(testPractical, senderInvite1, receiverInvite1);
        newCreatedInvite1.save();
        Invite newCreatedInvite2 = new Invite(testPractical, senderInvite2, receiverInvite2);
        newCreatedInvite2.save();

        // Test whether the method finds the correct amount of pending invites
        assertEquals(Invite.findPendingInvitesWhereUser(createdUser, testPractical).size(), 2);

        // Change the states of the invites
        newCreatedInvite1.setState(Invite.State.Accepted);
        newCreatedInvite1.save();
        newCreatedInvite2.setState(Invite.State.Rejected);
        newCreatedInvite2.save();

        // Check whether the return value of the method changes appropriately
        assertEquals(Invite.findPendingInvitesWhereUser(createdUser, testPractical).size(), 0);
    }

    /**
     * Test the method accept
     * Before
     * Invite1: CreatedUser1 -> CreatedUser2 : Pending
     * Invite2: CreatedUser2 -> CreatedUser3 : Pending
     * Invite3: CreatedUser1 -> CreatedUser3 : Pending
     * Accept invite2
     * After
     * Invite1: CreatedUser1 -> CreatedUser2 : Rejected
     * Invite2: CreatedUser2 -> CreatedUser3 : Accepted
     * Invite3: CreatedUser1 -> CreatedUser3 : Rejected
     */
    @Test
    public void testAcceptCase1() {
        // Create the users to put into the practicalgroup
        Practical createdPractical = new Practical("Created Practical", "Practical that has been created");
        createdPractical.save();
        User createdUser1 = new User("CreatedUser1", "LastName", "createduser1@example.com", User.Type.User);
        createdUser1.save();
        PracticalGroup practicalGroupUser1 = new PracticalGroup(createdPractical, createdUser1);
        practicalGroupUser1.save();
        User createdUser2 = new User("CreatedUser2", "LastName", "createduser2@example.com", User.Type.User);
        createdUser2.save();
        PracticalGroup practicalGroupUser2 = new PracticalGroup(createdPractical, createdUser2);
        practicalGroupUser2.save();
        User createdUser3 = new User("CreatedUser3", "LastName", "createduser3@example.com", User.Type.User);
        createdUser3.save();
        PracticalGroup practicalGroupUser3 = new PracticalGroup(createdPractical, createdUser3);
        practicalGroupUser3.save();

        Invite invite1 = new Invite(createdPractical, createdUser1, createdUser2);
        invite1.save();
        Invite invite2 = new Invite(createdPractical, createdUser2, createdUser3);
        invite2.save();
        Invite invite3 = new Invite(createdPractical, createdUser1, createdUser3);
        invite3.save();

        // Check if the invite that we are going to test is pending
        assertEquals(invite2.getState(), Invite.State.Pending);
        List<Invite> currentPendingInvitesSender = invite2.getSender().findPendingInvitesUser(invite2.getPractical());
        List<Invite> currentPendingInvitesReceiver = invite2.getReceiver().findPendingInvitesUser(invite2.getPractical());
        // Check whether there are more than 0 pending invites for both the sender and the receiver
        Logger.debug("currentPendingInvitesSender " + currentPendingInvitesSender.size());
        Logger.debug("currentPendingInvitesReceiver " + currentPendingInvitesReceiver.size());

        assert(currentPendingInvitesSender.size() > 0);
        assert(currentPendingInvitesReceiver.size() > 0);

        // Accept the invite
        invite2.accept();
        invite1 = Invite.findById(invite1.getId());
        invite2 = Invite.findById(invite2.getId());
        invite3 = Invite.findById(invite3.getId());
        List<Invite> newPendingInvitesSender = invite2.getSender().findPendingInvitesUser(invite1.getPractical());
        List<Invite> newPendingInvitesReceiver = invite2.getReceiver().findPendingInvitesUser(invite1.getPractical());

        // Check whether the right values have been decreased
        assertEquals(newPendingInvitesSender.size(), 0);
        assertEquals(newPendingInvitesReceiver.size(), 0);
        assertEquals(invite1.getState(), Invite.State.Rejected);
        assertEquals(invite2.getState(), Invite.State.Accepted);
        assertEquals(invite3.getState(), Invite.State.Rejected);
    }

    /**
     * Test the method accept
     * Before
     * Invite1: CreatedUser1 -> CreatedUser2 : Pending
     * Invite2: CreatedUser2 -> CreatedUser3 : Pending
     * Invite3: CreatedUser1 -> CreatedUser3 : Pending
     * Accept invite1
     * After
     * Invite1: CreatedUser1 -> CreatedUser2 : Accepted
     * Invite2: CreatedUser2 -> CreatedUser3 : Rejected
     * Invite3: CreatedUser1 -> CreatedUser3 : Pending
     */
    @Test
    public void testAcceptCase2() {
        // Create the users to put into the practicalgroup
        Practical createdPractical = new Practical("Created Practical", "Practical that has been created");
        createdPractical.save();
        User createdUser1 = new User("CreatedUser1", "LastName", "createduser1@example.com", User.Type.User);
        createdUser1.save();
        PracticalGroup practicalGroupUser1 = new PracticalGroup(createdPractical, createdUser1);
        practicalGroupUser1.save();
        User createdUser2 = new User("CreatedUser2", "LastName", "createduser2@example.com", User.Type.User);
        createdUser2.save();
        PracticalGroup practicalGroupUser2 = new PracticalGroup(createdPractical, createdUser2);
        practicalGroupUser2.save();
        User createdUser3 = new User("CreatedUser3", "LastName", "createduser3@example.com", User.Type.User);
        createdUser3.save();
        PracticalGroup practicalGroupUser3 = new PracticalGroup(createdPractical, createdUser3);
        practicalGroupUser3.save();

        Invite invite1 = new Invite(createdPractical, createdUser1, createdUser2);
        invite1.save();
        Invite invite2 = new Invite(createdPractical, createdUser2, createdUser3);
        invite2.save();
        Invite invite3 = new Invite(createdPractical, createdUser1, createdUser3);
        invite3.save();

        // Check if the invite that we are going to test is pending
        assertEquals(invite1.getState(), Invite.State.Pending);
        List<Invite> currentPendingInvitesSender = invite1.getSender().findPendingInvitesUser(invite2.getPractical());
        List<Invite> currentPendingInvitesReceiver = invite1.getReceiver().findPendingInvitesUser(invite2.getPractical());
        // Check whether there are more than 0 pending invites for both the sender and the receiver
        Logger.debug("currentPendingInvitesSender " + currentPendingInvitesSender.size());
        Logger.debug("currentPendingInvitesReceiver " + currentPendingInvitesReceiver.size());

        assert(currentPendingInvitesSender.size() > 0);
        assert(currentPendingInvitesReceiver.size() > 0);

        // Accept the invite
        invite1.accept();
        invite1 = Invite.findById(invite1.getId());
        invite2 = Invite.findById(invite2.getId());
        invite3 = Invite.findById(invite3.getId());
        List<Invite> newPendingInvitesSender = invite1.getSender().findPendingInvitesUser(invite1.getPractical());
        List<Invite> newPendingInvitesReceiver = invite1.getReceiver().findPendingInvitesUser(invite1.getPractical());

        // Check whether the right values have been decreased
        assertEquals(newPendingInvitesSender.size(), 1);
        assertEquals(newPendingInvitesReceiver.size(), 0);
        assertEquals(invite1.getState(), Invite.State.Accepted);
        assertEquals(invite2.getState(), Invite.State.Rejected);
        assertEquals(invite3.getState(), Invite.State.Pending);
    }


}
