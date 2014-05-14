package controllers;

import com.google.common.collect.ImmutableBiMap;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

/**
 * Created by Freek on 12/05/14.
 */
public class AuthenticationTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
        Ebean.save((List) Yaml.load("test-data.yml"));
    }

    @Test
    public void authenticationSuccess() {
        Result result = callAction(
                controllers.routes.ref.Authentication.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "test@test.com",
                        "password", "floor"))
        );

        assertEquals(303, status(result));
        assertEquals(User.byEmail("test@test.com").getId().toString(), session(result).get("user_id"));
    }

    @Test
    public void authenticationFail() {
        Result result = callAction(
                controllers.routes.ref.Authentication.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "test@test.com",
                        "password", "wrongpass"))
        );

        assertEquals(400, status(result));
        assertTrue(contentAsString(result).contains("Invalid user or password"));
        assertNotEquals(User.byEmail("test@test.com").getId().toString(), session(result).get("user_id"));
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
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(400, status(result));
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
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(400, status(result));
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
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(body));

        assertEquals(400, status(result));
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
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(body));


        User user = User.byEmail("mybob@example.com");
        User userAuth = User.authenticate("mybob@example.com", "myVeryGoodPass");

        assertEquals(303, status(result));
        assertNotNull(user);
        assertNotNull(userAuth);
        assertEquals(user, userAuth);
        assertEquals(user.getFirstName(), "Bob");
        assertEquals(user.getEmail(), "mybob@example.com");
    }
}
