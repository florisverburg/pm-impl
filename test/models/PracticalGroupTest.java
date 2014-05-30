package models;

import org.junit.*;
import org.junit.Test;
import play.test.WithApplication;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 */
public class PracticalGroupTest extends WithApplication {

    private PracticalGroup testPracticalGroup;
    private Practical testPractical;
    private User testUser;

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        testPractical = Practical.findByName("Programming");
        testUser = User.findByName("DefaultUser3");
        testPracticalGroup = PracticalGroup.findWithPracticalAndUser(testPractical, testUser);
    }

    /**
     * Method to test whether the creation of a skill has been successful
     */
    @Test
    public void testCreationSkill() {
        // Create Practical Group
        PracticalGroup createdPracticalGroup = new PracticalGroup(testPractical, testUser);
        createdPracticalGroup.save();

        createdPracticalGroup = PracticalGroup.findById(createdPracticalGroup.getId());

        // Check the values of the constructor and findbyid method
        assertEquals(createdPracticalGroup.getPractical(), testPractical);
        assertNotNull(createdPracticalGroup.getGroupMembers());
        assertEquals(createdPracticalGroup.getGroupMembers().size(), 1);
        assertEquals(createdPracticalGroup.getOwner().getId(), testUser.getId());
    }

    /**
     * Method to test the usage of the skill's setters
     */
    @Test
    public void testSetters() {
        Practical otherPractical = Practical.findByName("Documenting");
        User otherUser = User.findByName("DefaultUser2");

        // Set different values
        testPracticalGroup.setPractical(otherPractical);
        testPracticalGroup.setOwner(otherUser);
        testPractical.save();

        // Check the new values
        assertEquals(testPracticalGroup.getPractical().getId(), otherPractical.getId());
        assertEquals(testPracticalGroup.getOwner().getId(), otherUser.getId());
    }

    /**
     * Method to test addGroupMember
     */
    @Test
    public void testAddGroupMember() {
        // Create the users to put into the practicalgroup
        Practical createdPractical = new Practical("Created Practical", "Practical that has been created");
        createdPractical.save();
        User createdUser1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        createdUser1.save();
        User createdUser2 = new User("CreatedUser2", "LastName", "createduser@example.com", User.Type.User);
        createdUser2.save();
        // Save the users to the practical
        createdPractical.addUser(createdUser1);
        createdPractical.addUser(createdUser2);
        createdPractical.save();

        PracticalGroup createdPracticalGroup = new PracticalGroup(createdPractical, createdUser1);
        createdPracticalGroup.save();

        assertEquals(1, createdPracticalGroup.getGroupMembers().size());
        // Add a groupmember to the group
        createdPracticalGroup.addGroupMember(createdUser2);
        createdPracticalGroup.save();
        assertEquals(2, createdPracticalGroup.getGroupMembers().size());
    }

    /**
     * Method to test findWithPracticalAndUser
     */
    @Test
    public void testFindWithPracticalAndUser() {
        // Create the users to put into the practicalgroup
        Practical createdPractical = new Practical("Created Practical", "Practical that has been created");
        createdPractical.save();
        User createdUser1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        createdUser1.save();
        User createdUser2 = new User("CreatedUser2", "LastName", "createduser@example.com", User.Type.User);
        createdUser2.save();
        // Save the users to the practical
        createdPractical.addUser(createdUser1);
        createdPractical.addUser(createdUser2);
        createdPractical.save();


        // Create the first practicalGroup
        PracticalGroup createdPracticalGroup = new PracticalGroup(createdPractical, createdUser1);
        createdPracticalGroup.save();

        // Check the returned values of the method
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser1).getId(), createdPracticalGroup.getId());
        assertNull(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser2));

        // Add a groupmember to the group
        createdPracticalGroup.addGroupMember(createdUser2);
        createdPracticalGroup.save();
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser1).getId(), createdPracticalGroup.getId());
        // Check if you can find the group by one of its groupmembers
        assertEquals(PracticalGroup.findWithPracticalAndUser(createdPractical, createdUser2).getId(), createdPracticalGroup.getId());
    }

    /**
     * Method to test leaveGroup
     * Start:
     * CreatedPracticalGroup:
     * Owner: user1
     * Groupmembers: user2
     * leaveGroup(user1)
     * Two practical groups
     * #1:
     * Owner: user2
     * #2:
     * Owner: user1
     */
    @Test
    public void testLeaveGroupOwnerSuccess() {
        // Create the users to put into the practicalgroup
        Practical practical = new Practical("Created Practical", "Practical that has been created");
        practical.save();
        User user1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        user1.save();
        User user2 = new User("CreatedUser2", "LastName", "createduser@example.com", User.Type.User);
        user2.save();

        practical.addUser(user1);
        practical.addUser(user2);
        practical.save();

        PracticalGroup practicalGroup1 = new PracticalGroup(practical, user1);
        practicalGroup1.save();
        PracticalGroup practicalGroup2 = new PracticalGroup(practical, user2);
        practicalGroup2.save();

        Invite invite = new Invite(practical, user1, user2);
        invite.accept();

        practicalGroup1.refresh();
        invite.refresh();
        assertEquals(2, practicalGroup1.getGroupMembers().size());
        assertEquals(Invite.State.Accepted, invite.getState());

        practicalGroup1.leaveGroup(user1);

        practicalGroup1 = PracticalGroup.findById(practicalGroup1.getId());
        PracticalGroup newPracticalGroup = PracticalGroup.findWithPracticalAndUser(practical, user2);
        assertEquals(1, newPracticalGroup.getGroupMembers().size());
        assertEquals(1, practicalGroup1.getGroupMembers().size());
        invite.refresh();
        assertEquals(Invite.State.Rejected, invite.getState());
    }

    /**
     * Method to test leaveGroup
     * Start:
     * CreatedPracticalGroup:
     * Owner: user1
     * Groupmembers: user2
     * leaveGroup(user2)
     * Two practical groups
     * #1:
     * Owner: user1
     * #2:
     * Owner: user2
     */
    @Test
    public void testLeaveGroupGroupMemberSuccess() {
        // Create the users to put into the practicalgroup
        Practical practical = new Practical("Created Practical", "Practical that has been created");
        practical.save();
        User user1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        user1.save();
        User user2 = new User("CreatedUser2", "LastName", "createduser@example.com", User.Type.User);
        user2.save();

        practical.addUser(user1);
        practical.addUser(user2);
        practical.save();

        PracticalGroup practicalGroup1 = new PracticalGroup(practical, user1);
        practicalGroup1.save();
        PracticalGroup practicalGroup2 = new PracticalGroup(practical, user2);
        practicalGroup2.save();

        Invite invite = new Invite(practical, user1, user2);
        invite.accept();

        practicalGroup1.refresh();
        invite.refresh();
        assertEquals(2, practicalGroup1.getGroupMembers().size());
        assertEquals(Invite.State.Accepted, invite.getState());

        practicalGroup1.leaveGroup(user2);

        practicalGroup1 = PracticalGroup.findById(practicalGroup1.getId());
        PracticalGroup newPracticalGroup = PracticalGroup.findWithPracticalAndUser(practical, user2);
        assertEquals(1, newPracticalGroup.getGroupMembers().size());
        assertEquals(1, practicalGroup1.getGroupMembers().size());
    }

    @Test
    public void testLeaveGroupErrorSize() {
        // Create the users to put into the practicalgroup
        Practical practical = new Practical("Created Practical", "Practical that has been created");
        practical.save();
        User user1 = new User("CreatedUser1", "LastName", "createduser@example.com", User.Type.User);
        user1.save();

        practical.addUser(user1);
        practical.save();

        PracticalGroup practicalGroup = new PracticalGroup(practical, user1);
        practicalGroup.save();
        assertEquals(1, practicalGroup.getGroupMembers().size());

        practicalGroup.leaveGroup(user1);
        practicalGroup = PracticalGroup.findById(practicalGroup.getId());
        assertEquals(1, practicalGroup.getGroupMembers().size());
    }
}
