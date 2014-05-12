package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import com.avaje.ebean.*;

/**
 * Created by Freek on 12/05/14.
 */
public class UserTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveUser() {
        new User("bob").save();
        User bob = Ebean.find(User.class).where().eq("name", "bob").findUnique();
        assertNotNull(bob);
        assertEquals("bob", bob.getName());
    }
}
