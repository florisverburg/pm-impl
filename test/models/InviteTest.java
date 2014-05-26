package models;

import org.junit.Before;
import org.junit.Test;
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
}
