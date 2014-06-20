package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.*;


/**
 * Simple info that tests the User model
 */
public class UserTest extends WithApplication {

    private User testUser1;
    private User testUser2;
    private User testUser3;

    /**
     * Setup method for the UserTest
     */
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testUser1 = User.findByName("DefaultUser1");
        testUser2 = User.findByName("DefaultUser2");
        testUser3 = User.findByName("Unverified");
    }

    /**
     * Method to info whether the creation of an user has been successful
     */
    @Test
    public void testCreationUser() {
        // Create a new user
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.Admin);
        createdUser.save();

        // Retrieve user from database
        User retrievedUser = User.findById(createdUser.getId());

        // Check the values of the setUp() method
        // Check the new values
        assertEquals(createdUser.getFirstName(), "CreatedUser");
        assertEquals(createdUser.getLastName(), "LastName");
        assertEquals(createdUser.getEmail(), "createduser@example.com");
        assertEquals(createdUser.getType(), User.Type.Admin);
        assertEquals(retrievedUser.getToken().getClass(), String.class);
        assertFalse(PasswordIdentity.contains(createdUser));
    }

    @Test
    public void profileImage() {
        assertEquals(testUser1.getProfileImage(), User.ProfileImage.None);
        assertTrue(testUser1.getProfileImageUrl().contains("f=y"));
        assertEquals(testUser2.getProfileImage(), User.ProfileImage.Gravatar);
        assertFalse(testUser2.getProfileImageUrl().contains("f=y"));

        testUser1.setProfileImage(User.ProfileImage.Gravatar);
        testUser2.setProfileImage(User.ProfileImage.None);

        assertEquals(testUser1.getProfileImage(), User.ProfileImage.Gravatar);
        assertFalse(testUser1.getProfileImageUrl().contains("f=y"));
        assertEquals(testUser2.getProfileImage(), User.ProfileImage.None);
        assertTrue(testUser2.getProfileImageUrl().contains("f=y"));
    }

    /**
     * Method to info the usage of the user's setters
     */
    @Test
    public void testSetters() {
        // Set different values
        testUser1.setFirstName("SetUser");
        testUser1.setLastName("SetUser");
        testUser1.setEmail("setuser@example.com");
        testUser1.setType(User.Type.Admin);
        testUser1.setToken("newToken");
        testUser1.save();

        // Retrieve testUser1 from the database
        User retrievedUser = User.findById(testUser1.getId());

        // Check the new values
        assertEquals(retrievedUser.getFirstName(), "SetUser");
        assertEquals(retrievedUser.getLastName(), "SetUser");
        assertEquals(retrievedUser.getEmail(), "setuser@example.com");
        assertEquals(retrievedUser.getType(), User.Type.Admin);
        assertEquals(retrievedUser.getToken(), "newToken");
    }

    /**
     * Test getByEmailMethod
     */
    @Test
    public void findByEmail() {
        // Test whether the findByEmail returns the correct values
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.Admin);
        createdUser.save();
        createdUser = User.findByEmail(createdUser.getEmail());
        // Check the new values
        assertEquals(createdUser.getFirstName(), "CreatedUser");
        assertEquals(createdUser.getLastName(), "LastName");
        assertEquals(createdUser.getEmail(), "createduser@example.com");
        assertEquals(createdUser.getType(), User.Type.Admin);


        // retrieve the user createdUser by his email and by his id
        User retrievedUserById = User.findById(createdUser.getId());
        User retrievedUserByEmail = User.findByEmail(createdUser.getEmail());

        // Test whether the two functions return the same data
        assertEquals(retrievedUserById, retrievedUserByEmail);
        assertEquals(retrievedUserById.getId(), retrievedUserByEmail.getId());
        assertEquals(retrievedUserById.getEmail(), retrievedUserByEmail.getEmail());
    }

    /**
     * Skill many-to-many relationship info
     */
    @Test
    public void testSkillManyToManyRelationship() {
        User createdUser = new User("CreatedUser", "LastName", "createduser@example.com", User.Type.User);
        createdUser.save();
        // Get skills
        Skill programming = Skill.findByName("Programming");
        Skill documenting = Skill.findByName("Documenting");

        User retrievedUser = User.findById(createdUser.getId());
        // Check values
        assertEquals(SkillValueUser.findByUser(retrievedUser).size(), 0);

        // Add skill to user
        SkillValueUser uSkill = new SkillValueUser(retrievedUser, programming, 8);
        uSkill.save();

        retrievedUser = User.findById(createdUser.getId());
        // Check values
        assertEquals(SkillValueUser.findByUser(retrievedUser).size(), 1);
        assertEquals(SkillValueUser.findByUser(retrievedUser).get(0).getSkill().getName(), programming.getName());
    }
    
    @Test
    public void authenticateSuccess() {
        User createdUser = new User("CreatedUser", "lastName", "createduser@example.com", User.Type.User);
        createdUser.save();
        new PasswordIdentity(createdUser, createdUser.getEmail(), "florina").save();

        assertNotNull(PasswordIdentity.authenticate(createdUser.getEmail(), "florina"));
        assertEquals(PasswordIdentity.authenticate(createdUser.getEmail(), "florina").getUser(), createdUser);
    }

    @Test
    public void authenticateFail() {
        User createdUser = new User("CreatedUser", "lastName", "createduser@example.com", User.Type.User);
        createdUser.save();
        new PasswordIdentity(createdUser, createdUser.getEmail(), "florina").save();

        assertNull(PasswordIdentity.authenticate("floor@wrongemail.com", "florina"));
        assertNull(PasswordIdentity.authenticate(createdUser.getEmail(), "wrongpass"));
    }

    @Test
    public void typeTest() {
        assertNotEquals(testUser1.getType(), User.Type.Teacher);
        assertEquals(testUser1.getType(), User.Type.User);
    }

    @Test
    public void passwordTest() {
        assertTrue(PasswordIdentity.contains(testUser1));
        assertFalse(PasswordIdentity.contains(testUser3));
    }
}
