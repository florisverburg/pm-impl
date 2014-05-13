package controllers;

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
        Result result = callAction(
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "name", "",
                        "email", "nonvalid!@#43844",
                        "password", "myVeryGoodPas",
                        "passwordRepeat", "myVeryGoodPass"
                ))
        );

        assertEquals(400, status(result));
        assertTrue(contentAsString(result).contains("Valid email required"));
        assertTrue(contentAsString(result).contains("This field is required"));
    }

    @Test
    public void registrationFailPassword() {
        Result result = callAction(
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "name", "Bob",
                        "email", "mybob@example.com",
                        "password", "myVeryGoodPass",
                        "passwordRepeat", "wrong"
                ))
        );

        assertEquals(400, status(result));
        assertTrue(contentAsString(result).contains("The passwords don&#x27;t match"));;
    }

    @Test
    public void registrationFailPasswordLength() {
        Result result = callAction(
                routes.ref.Authentication.registration(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "name", "Bob",
                        "email", "mybob@example.com",
                        "password", "bad",
                        "passwordRepeat", "bad"
                ))
        );

        assertEquals(400, status(result));
        assertTrue(contentAsString(result).contains("Minimum length is"));;
    }
}
