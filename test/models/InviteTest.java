package models;

import com.avaje.ebean.Ebean;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class to info the model representation of the database table of invite
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

    @Test
    public void testFindByPracticalSenderReceiver() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();

        assertEquals(invite, Invite.findById(invite.getId()));
        assertEquals(invite, Invite.findByPracticalSenderReceiver(practical, user1, user2));
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

        // Check if the invite that we are going to info is pending
        assertEquals(invite2.getState(), Invite.State.Pending);

        // Accept the invite
        invite2.accept();
        invite1 = Invite.findById(invite1.getId());
        invite2 = Invite.findById(invite2.getId());
        invite3 = Invite.findById(invite3.getId());
        practicalGroupUser1 = PracticalGroup.findById(practicalGroupUser1.getId());
        practicalGroupUser2 = PracticalGroup.findById(practicalGroupUser2.getId());
        practicalGroupUser3 = PracticalGroup.findById(practicalGroupUser3.getId());
        List<Invite> newPendingInvitesSender = invite2.getSender().findPendingInvitesUser(invite1.getPractical());
        List<Invite> newPendingInvitesReceiver = invite2.getReceiver().findPendingInvitesUser(invite1.getPractical());

        // Check whether the right values have been decreased
        assertEquals(newPendingInvitesSender.size(), 0);
        assertEquals(newPendingInvitesReceiver.size(), 0);
        assertEquals(invite1.getState(), Invite.State.Rejected);
        assertEquals(invite2.getState(), Invite.State.Accepted);
        assertEquals(invite3.getState(), Invite.State.Rejected);
        assertNull(practicalGroupUser3);
        assertEquals(practicalGroupUser2.getGroupMembers().size(), 2);
        assertEquals(practicalGroupUser1.getGroupMembers().size(), 1);
    }

    /**
     * Test the method accept
     * Before
     * Invite1: CreatedUser1 -> CreatedUser2 : Pending
     * Invite2: CreatedUser2 -> CreatedUser3 : Pending
     * Invite3: CreatedUser1 -> CreatedUser3 : Pending
     * Invite4: CreatedUser4 -> CreatedUser1 : Pending
     * Invite5: CreatedUser4 -> Createduser2 : Pending
     * Accept invite1
     * After
     * Invite1: CreatedUser1 -> CreatedUser2 : Accepted
     * Invite2: CreatedUser2 -> CreatedUser3 : Rejected
     * Invite3: CreatedUser1 -> CreatedUser3 : Pending
     * Invite4: CreatedUser4 -> CreatedUser1 : Rejected
     * Invite5: CreatedUser5 -> CreatedUser2 : Rejected
     * Accept invite3
     * After
     * Invite1: CreatedUser1 -> CreatedUser2 : Accepted
     * Invite2: CreatedUser2 -> CreatedUser3 : Rejected
     * Invite3: CreatedUser1 -> CreatedUser3 : Accepted
     * Invite4: CreatedUser4 -> CreatedUser1 : Rejected
     * Invite5: CreatedUser5 -> CreatedUser2 : Rejected
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
        User createdUser4 = new User("CreatedUser4", "LastName", "createduser4@example.com", User.Type.User);
        createdUser4.save();
        PracticalGroup practicalGroupUser4 = new PracticalGroup(createdPractical, createdUser4);
        practicalGroupUser4.save();

        Invite invite1 = new Invite(createdPractical, createdUser1, createdUser2);
        invite1.save();
        Invite invite2 = new Invite(createdPractical, createdUser2, createdUser3);
        invite2.save();
        Invite invite3 = new Invite(createdPractical, createdUser1, createdUser3);
        invite3.save();
        Invite invite4 = new Invite(createdPractical, createdUser4, createdUser1);
        invite4.save();
        Invite invite5 = new Invite(createdPractical, createdUser4, createdUser2);
        invite5.save();

        // Check if the invite that we are going to info is pending
        assertEquals(invite1.getState(), Invite.State.Pending);

        // Accept the invite
        invite1.accept();
        // Reload values
        invite1 = Invite.findById(invite1.getId());
        invite2 = Invite.findById(invite2.getId());
        invite3 = Invite.findById(invite3.getId());
        invite4 = Invite.findById(invite4.getId());
        invite5 = Invite.findById(invite5.getId());
        practicalGroupUser1 = PracticalGroup.findById(practicalGroupUser1.getId());
        practicalGroupUser2 = PracticalGroup.findById(practicalGroupUser2.getId());
        practicalGroupUser3 = PracticalGroup.findById(practicalGroupUser3.getId());
        practicalGroupUser4 = PracticalGroup.findById(practicalGroupUser4.getId());

        // Check whether the right values have been decreased
        assertEquals(invite1.getState(), Invite.State.Accepted);
        assertEquals(invite2.getState(), Invite.State.Rejected);
        assertEquals(invite3.getState(), Invite.State.Pending);
        assertEquals(invite4.getState(), Invite.State.Rejected);
        assertEquals(invite5.getState(), Invite.State.Rejected);
        assertEquals(practicalGroupUser1.getGroupMembers().size(), 2);
        assertNull(practicalGroupUser2);
        assertEquals(practicalGroupUser3.getGroupMembers().size(), 1);
        assertEquals(practicalGroupUser4.getGroupMembers().size(), 1);

        invite3.accept();
        // Reload values
        invite1 = Invite.findById(invite1.getId());
        invite2 = Invite.findById(invite2.getId());
        invite3 = Invite.findById(invite3.getId());
        practicalGroupUser1 = PracticalGroup.findById(practicalGroupUser1.getId());
        //practicalGroupUser2 = PracticalGroup.findById(practicalGroupUser2.getId());
        practicalGroupUser3 = PracticalGroup.findById(practicalGroupUser3.getId());
        practicalGroupUser4 = PracticalGroup.findById(practicalGroupUser4.getId());

        // Check whether the right values have been decreased
        assertEquals(invite1.getState(), Invite.State.Accepted);
        assertEquals(invite2.getState(), Invite.State.Rejected);
        assertEquals(invite3.getState(), Invite.State.Accepted);
        assertEquals(invite4.getState(), Invite.State.Rejected);
        assertEquals(practicalGroupUser1.getGroupMembers().size(), 3);
        assertNull(practicalGroupUser2);
        assertNull(practicalGroupUser3);
        assertEquals(practicalGroupUser4.getGroupMembers().size(), 1);
    }

    @Test
    public void testRejectSuccess() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();

        assertEquals(Invite.State.Pending, invite.getState());
        invite.reject();
        invite.findById(invite.getId());
        assertEquals(Invite.State.Rejected, invite.getState());
    }

    @Test
    public void testRejectWrongState() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        invite.setState(Invite.State.Accepted);
        invite.save();

        assertEquals(Invite.State.Accepted, invite.getState());
        invite.reject();
        invite.findById(invite.getId());
        assertEquals(Invite.State.Accepted, invite.getState());
    }

    /**
     * Test for checkinvite method
     */
    @Test
    public void testCheckInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        // check if basic is correct
        assertTrue(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));

        Invite invite1 = new Invite(practical, user1, user2);
        invite1.save();

        // check if result is correct
        assertFalse(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));

        Ebean.delete(invite1);
        Invite invite2 = new Invite(practical, user2, user1);
        invite2.save();

        // check if result the other way is also correct
        assertTrue(Invite.checkInvite(user1, user2, practical));
        assertFalse(Invite.checkInvite(user2, user1, practical));
    }

    /**
     * Test to check send invite method works properly
     */
    @Test
    public void testSendInviteSuccess() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        assertTrue(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));

        // Send the invite
        assertNotNull(Invite.sendInvite(practical, user1, user2));
        // Check whether the invite has been truly added
        assertFalse(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));
    }

    /**
     * Test
     */
    @Test
    public void testSendInviteAlready() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite1 = new Invite(practical, user1, user2);
        invite1.save();

        assertFalse(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));

        // Check whether the method returns the right value
        assertNull(Invite.sendInvite(practical, user1, user2));

        // Check whether there still hasn't been added an invite
        assertFalse(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));
    }

    /**
     * Test
     */
    @Test
    public void testSendInviteEqualSenderReceiver() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        assertTrue(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));

        // Check whether the method returns the right value
        assertNull(Invite.sendInvite(practical, user1, user1));

        // Check whether there still hasn't been added an invite
        assertTrue(Invite.checkInvite(user1, user2, practical));
        assertTrue(Invite.checkInvite(user2, user1, practical));
    }

    @Test
    public void testWithdrawSuccess() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();

        assertEquals(Invite.State.Pending, invite.getState());
        invite.withdraw();
        invite.findById(invite.getId());
        assertEquals(Invite.State.Withdrawn, invite.getState());
    }

    @Test
    public void testWithdrawWrongState() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        invite.setState(Invite.State.Accepted);
        invite.save();

        assertEquals(Invite.State.Accepted, invite.getState());
        invite.withdraw();
        invite.findById(invite.getId());
        assertEquals(Invite.State.Accepted, invite.getState());
    }

    @Test
    public void testResendSuccessSender() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        invite.setState(Invite.State.Withdrawn);
        invite.save();

        assertEquals(Invite.State.Withdrawn, invite.getState());
        invite.findById(invite.getId());
        invite.resend(user1);
        Invite resultInvite = Invite.findById(invite.getId());
        assertEquals(Invite.State.Pending, resultInvite.getState());
        assertEquals(user1, resultInvite.getSender());
        assertEquals(user2, resultInvite.getReceiver());
    }

    @Test
    public void testResendSuccessReceiver() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        invite.setState(Invite.State.Withdrawn);
        invite.save();

        assertEquals(Invite.State.Withdrawn, invite.getState());
        invite.resend(user2);

        //assertNull(Invite.findById(invite.getId()));
        invite = Invite.findByPracticalSenderReceiver(practical, user2, user1);
        assertEquals(Invite.State.Pending, invite.getState());
        assertEquals(user2, invite.getSender());
        assertEquals(user1, invite.getReceiver());
    }

    @Test
    public void testResendFailureState() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");

        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        assertEquals(Invite.State.Pending, invite.getState());
        invite.setState(Invite.State.Accepted);
        invite.save();

        invite = Invite.findById(invite.getId());
        assertEquals(Invite.State.Accepted, invite.getState());
        invite.resend(user2);

        invite = invite.findById(invite.getId());
        assertEquals(Invite.State.Accepted, invite.getState());
        assertEquals(user1, invite.getSender());
        assertEquals(user2, invite.getReceiver());
    }
}
