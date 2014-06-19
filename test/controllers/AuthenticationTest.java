package controllers;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.mvc.*;
import static play.test.Helpers.*;
import com.google.common.collect.ImmutableMap;
import play.test.WithApplication;

/**
 * Created by Freek on 12/05/14.
 */
public class AuthenticationTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void authenticationPage() {
        Result result = callAction(
                routes.ref.AuthenticationController.login()
        );

        assertEquals(OK, status(result));
    }

    @Test
    public void authenticationSuccess() {
        Result result = callAction(
                routes.ref.AuthenticationController.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "defaultuser1@example.com",
                        "password", "defaultuser1"))
        );
        assertEquals(SEE_OTHER, status(result));
        assertEquals(User.findByEmail("defaultuser1@example.com").getId().toString(), session(result).get("user_id"));
    }

    @Test
    public void authenticationFail() {
        Result result = callAction(
                routes.ref.AuthenticationController.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "defaultuser1@example.com",
                        "password", "wrongpass"))
        );

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains("Wrong login information supplied"));
        assertNotEquals(User.findByEmail("defaultuser1@example.com").getId().toString(), session(result).get("user_id"));
    }

    @Test
    public void registrationFailNameEmail() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "");
        body.put("lastName", "");
        body.put("language", "english");
        body.put("email", "nonValidMail");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        Result result = callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains("Valid email required"));
        assertTrue(contentAsString(result).contains("This field is required"));
    }

    @Test
    public void registrationFailPassword() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "wrong");

        Result result = callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains("The passwords don&#x27;t match"));
    }

    @Test
    public void registrationFailPasswordLength() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "bad");
        body.put("passwordRepeat", "bad");

        Result result = callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(BAD_REQUEST, status(result));
        assertTrue(contentAsString(result).contains("Minimum length is"));
    }

    @Test
    public void registrationSuccess() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        Result result = callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));


        User user = User.findByEmail("mybob@example.com");
        PasswordIdentity identity = PasswordIdentity.authenticate("mybob@example.com", "myVeryGoodPass");
        assertNotNull(identity);
        User userAuth = identity.getUser();

        assertEquals(SEE_OTHER, status(result));
        assertNotNull(user);
        assertNotNull(userAuth);
        assertEquals(user, userAuth);
        assertEquals(user.getFirstName(), "Bob");
        assertEquals(user.getEmail(), "mybob@example.com");
    }

    @Test
    public void notAuthenticated() {
        Result result = callAction(
                routes.ref.AuthenticationController.logout(),
                fakeRequest()
        );
        assertEquals(SEE_OTHER, status(result));
        assertEquals("/login", header("Location", result));
    }

    @Test
    public void authenticated() {
        Result result = callAction(
                routes.ref.AuthenticationController.logout(),
                fakeRequest().withSession("user_id", "500")
        );
        assertEquals(SEE_OTHER, status(result));
    }

    @Test
    public void tokenAdded() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));


        User user = User.findByEmail("mybob@example.com");
        PasswordIdentity identity = PasswordIdentity.authenticate("mybob@example.com", "myVeryGoodPass");
        assertNotNull(identity);
        User userAuth = identity.getUser();

        assertNotNull(user);
        assertNotNull(userAuth);
        assertNotNull(user.getToken());
    }

    @Test
    public void notLoggedInBeforeVerification() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        Result result = callAction(
                routes.ref.AuthenticationController.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "mybob@example.com",
                        "password", "myVeryGoodPass"))
        );

        assertTrue(contentAsString(result).contains("The email address is not yet verified"));
        assertNotEquals(User.findByEmail("mybob@example.com").getId().toString(), session(result).get("user_id"));
    }

    @Test
    public void unknownValidation() {
        Result result = callAction(routes.ref.AuthenticationController.verify("test@nonvalidemail.com", "nonValid"));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("error.unknownValidation", flash(result).get("error"));
    }

    @Test
    public void unknownValidation2() {
        Result result = callAction(routes.ref.AuthenticationController.verify("unverifiedemail@example.com", null));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("error.unknownValidation", flash(result).get("error"));
    }

    @Test
    public void wrongToken() {
        Result result = callAction(routes.ref.AuthenticationController.verify("unverifiedemail@example.com", "nonValid"));

        assertEquals(SEE_OTHER, status(result));
        assertEquals("error.wrongToken", flash(result).get("error"));
    }

    @Test
    public void tokenDeletedAfterVerification() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));


        User user = User.findByEmail("mybob@example.com");
        PasswordIdentity identity = PasswordIdentity.authenticate("mybob@example.com", "myVeryGoodPass");
        assertNotNull(identity);
        User userAuth = identity.getUser();


        assertNotNull(user);
        assertNotNull(userAuth);
        assertNotNull(user.getToken());
        assertNotNull(user.getEmail());

        callAction(routes.ref.AuthenticationController.verify(user.getEmail(), user.getToken()));

        user = User.findByEmail("mybob@example.com");

        assertNotNull(user);
        assertNotNull(userAuth);
        assertNull(user.getToken());
    }

    @Test
    public void loggedInAfterVerification() {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("firstName", "Bob");
        body.put("lastName", "Verburg");
        body.put("language", "english");
        body.put("email", "mybob@example.com");
        body.put("password", "myVeryGoodPass");
        body.put("passwordRepeat", "myVeryGoodPass");

        callAction(
                routes.ref.AuthenticationController.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        User user = User.findByEmail("mybob@example.com");
        callAction(routes.ref.AuthenticationController.verify(user.getEmail(), user.getToken()));

        Result result = callAction(
                routes.ref.AuthenticationController.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "mybob@example.com",
                        "password", "myVeryGoodPass"))
        );

        assertEquals(User.findByEmail("mybob@example.com").getId().toString(), session(result).get("user_id"));
    }

}
