package controllers;

import com.google.common.collect.ImmutableMap;
import models.*;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.mvc.*;
import play.test.WithApplication;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

/**
 * Created by Freek on 12/05/14.
 */
public class ProfileTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void viewProfileDenied() {
        Result result = callAction(
                routes.ref.Profile.view()
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("authentication.unauthorized", flash(result).get("error"));
    }

    @Test
    public void viewProfileSuccess() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.view(),
                fakeRequest().withSession("user_id", user.getId().toString())
        );

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(user.getFullName()));
        assertTrue(contentAsString(result).contains(user.getType().toString()));
        assertTrue(contentAsString(result).contains(user.getEmail()));
        assertTrue(contentAsString(result).contains(user.getProfileImageUrl()));
    }

    @Test
    public void editProfileDenied() {
        Result result = callAction(
                routes.ref.Profile.edit()
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("authentication.unauthorized", flash(result).get("error"));
    }

    @Test
    public void editProfileSuccess() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.edit(),
                fakeRequest().withSession("user_id", user.getId().toString())
        );

        assertEquals(OK, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains(user.getLastName()));
        assertTrue(contentAsString(result).contains(user.getEmail()));
    }

    @Test
    public void saveProfileRequired() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(ImmutableMap.of(
                                "firstName", user.getFirstName(),
                                "lastName", "",
                                "email", "trolololo@trol.com"))
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains("trolololo@trol.com"));
        assertTrue(contentAsString(result).contains("This field is required"));
    }

    @Test
    public void saveProfileInvalidEmail() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(ImmutableMap.of(
                                "firstName", user.getFirstName(),
                                "lastName", user.getLastName(),
                                "email", "nonvalidemailaddress!@#"))
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains(user.getLastName()));
        assertTrue(contentAsString(result).contains("nonvalidemailaddress!@#"));
        assertTrue(contentAsString(result).contains("Valid email required"));
    }

    @Test
    public void saveProfileEmailInUse() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(ImmutableMap.of(
                                "firstName", user.getFirstName(),
                                "lastName", user.getLastName(),
                                "email", "mistertest@test.com",
                                "profileImage", "Gravatar"))
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains(user.getLastName()));
        assertTrue(contentAsString(result).contains("mistertest@test.com"));
        assertTrue(contentAsString(result).contains("The email address is already registered"));
    }

    @Test
    public void saveProfileSuccess() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(ImmutableMap.of(
                                "firstName", "NewFirst Name",
                                "lastName", "New last Name",
                                "email", "newemail@test.com",
                                "profileImage", "Gravatar"))
        );
        User newUser = User.findByEmail("newemail@test.com");

        assertEquals(SEE_OTHER, status(result));
        assertEquals("profile.saved", flash(result).get("success"));
        assertEquals("NewFirst Name New last Name", newUser.getFullName());
        assertEquals(user.getId(), newUser.getId());
        assertEquals("newemail@test.com", newUser.getEmail());
    }

    @Test
    public void saveProfileWrongPass() {
        User user = User.findByEmail("test@test.com");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());
        body.put("email", user.getEmail());
        body.put("password", "veryGoodPassword");
        body.put("passwordRepeat", "notTheSamePassword");
        body.put("profileImage", "Gravatar");

        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(body)
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains(user.getLastName()));
        assertTrue(contentAsString(result).contains(user.getEmail()));
        assertTrue(contentAsString(result).contains("The passwords don&#x27;t match"));
    }

    @Test
    public void saveProfileWrongPassShort() {
        User user = User.findByEmail("test@test.com");
        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(ImmutableMap.of(
                                "firstName", user.getFirstName(),
                                "lastName", user.getLastName(),
                                "email", user.getEmail(),
                                "password", "short",
                                "passwordRepeat", "short"))
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains(user.getFirstName()));
        assertTrue(contentAsString(result).contains(user.getLastName()));
        assertTrue(contentAsString(result).contains(user.getEmail()));
        assertTrue(contentAsString(result).contains("Minimum length is"));
    }

    @Test
    public void saveProfileFailNone() {
        User user = User.findByEmail("admin@test.com");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());
        body.put("email", user.getEmail());
        body.put("password", "myNewPassword");
        body.put("passwordRepeat", "myNewPassword");
        body.put("profileImage", "Gravatar");

        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(body)
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("profile.saved", flash(result).get("success"));
        assertNull(User.authenticate(user.getEmail(), "myNewPassword"));
    }

    @Test
    public void saveProfileSuccessPass() {
        User user = User.findByEmail("test@test.com");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());
        body.put("email", user.getEmail());
        body.put("password", "myNewPassword");
        body.put("passwordRepeat", "myNewPassword");
        body.put("profileImage", "None");

        Result result = callAction(
                routes.ref.Profile.save(),
                fakeRequest()
                        .withSession("user_id", user.getId().toString())
                        .withFormUrlEncodedBody(body)
        );

        assertEquals(SEE_OTHER, status(result));
        assertEquals("profile.saved", flash(result).get("success"));
        assertNotNull(User.authenticate(user.getEmail(), "myNewPassword"));
    }
}
