package controllers;

import models.*;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import static org.junit.Assert.*;
import play.mvc.*;
import static play.test.Helpers.*;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 28-5-2014.
 */
public class InviteControllerTest extends WithApplication {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void viewInviteDenied() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.view(invite.getId())
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("authentication.unauthorized", flash(result).get("error"));
    }

    @Test
    public void viewInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.view(invite.getId()),
                fakeRequest().withSession("user_id", user1.getId().toString())
        );

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(invite.getPractical().getName()));
        assertTrue(contentAsString(result).contains(user1.getFullName()));
        assertTrue(contentAsString(result).contains(user2.getFullName()));
    }

    @Test
    public void acceptInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.acceptInvite(invite.getId()),
                fakeRequest().withSession("user_id", user1.getId().toString())
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("invite.accepted", flash(result).get("success"));
    }

    @Test
    public void withdrawInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.withdrawInvite(invite.getId()),
                fakeRequest().withSession("user_id", user1.getId().toString())
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("invite.withdrawn", flash(result).get("success"));
    }

    @Test
    public void resendInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.resendInvite(invite.getId()),
                fakeRequest().withSession("user_id", user1.getId().toString())
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("invite.resend", flash(result).get("success"));
    }

    @Test
    public void rejectInvite() {
        Practical practical = Practical.findByName("InviteControllerTest");
        User user1 = User.findByName("DefaultUser1");
        User user2 = User.findByName("DefaultUser2");
        Invite invite = new Invite(practical, user1, user2);
        invite.save();
        Result result = callAction(
                routes.ref.InviteController.rejectInvite(invite.getId()),
                fakeRequest().withSession("user_id", user1.getId().toString())
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("invite.reject", flash(result).get("success"));
    }
}
