package controllers;

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
    public void authenticateSuccess() {
        Result result = callAction(
                controllers.routes.ref.Authentication.authenticate(),
                fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                        "email", "test",
                        "password", "test"))
        );
        assertEquals(302, status(result));
        assertEquals("bob@example.com", session(result).get("email"));
    }
}
