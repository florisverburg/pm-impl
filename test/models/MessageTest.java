package models;

import org.junit.*;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Freek on 07/06/14.
 */
public class MessageTest {

    List<Invite> listOfPendingInvites;
    Practical testPractical;
    User testUser;
    User testUser1;
    Message testMessage;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));

        testUser = User.findByName("DefaultUser1");
        testUser1 = User.findByName("Unverified");
        testPractical = Practical.findByName("Programming");
        listOfPendingInvites = Invite.findPendingInvitesWhereUser(testUser, testPractical);
        testMessage = Message.find.all().get(0);
    }

    @Test
    public void createNew() throws InterruptedException {
        Message msg = new Message(listOfPendingInvites.get(0), testUser, "This is a new message!");
        msg.save();

        // Sleep for 4 seconds
        Thread.sleep(2000);

        // Create a new message
        Message msg2 = new Message(listOfPendingInvites.get(0), testUser, "This is a new message!");
        msg.save();

        assertTrue(listOfPendingInvites.get(0).getMessages().contains(msg));
        assertTrue(msg.getId() > 0);
        assertEquals(msg.getInvite(), listOfPendingInvites.get(0));
        assertEquals(msg.getUser(), testUser);
        assertEquals(msg.getMessage(), "This is a new message!");
        assertTrue(msg.compareTo(msg2) < 0);
        assertEquals(msg.getInvite().getSortedMessages().get(1), msg);
    }

    @Test
    public void change() {
        java.util.Date date= new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        assertNotNull(testMessage);
        testMessage.setMessage("New changes message...");
        testMessage.setUser(testUser1);
        testMessage.setInvite(listOfPendingInvites.get(1));
        testMessage.setTimestamp(timestamp);
        testMessage.save();

        assertTrue(listOfPendingInvites.get(1).getMessages().contains(testMessage));
        assertTrue(testMessage.getId() > 0);
        assertEquals(testMessage.getInvite(), listOfPendingInvites.get(1));
        assertEquals(testMessage.getUser(), testUser1);
        assertEquals(testMessage.getMessage(), "New changes message...");
        assertEquals(testMessage.getTimestamp(), timestamp);
        assertEquals(testMessage.getTime(), new SimpleDateFormat("dd/MM/yyyy hh:mm").format(timestamp));
    }

    @Test
    public void clearAll() {
        listOfPendingInvites.get(0).setMessages(null);
        listOfPendingInvites.get(0).save();

        assertTrue(listOfPendingInvites.get(0).getMessages().isEmpty());
    }
}
