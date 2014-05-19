package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by Marijn Goedegebure on 19-5-2014.
 * Class to test the model representation of the database table of invite
 */
public class InviteTest extends WithApplication {

    private User bob;
    private User hendrik;

    private Practical programmingAssignment;
    private Practical documentingAssingment;

    private Invite programmingBobToHendrik;
    private Invite documentingHendrikToBoB;

    @Before
    public void setUp() {

        // Create a new user
        bob = new User("Bob", "Verburg", "bob@example.com");
        bob.save();

        // Create a new user
        hendrik = new User("Hendrik","Tienen","hendrik@example.com");
        hendrik.save();

        // Create a new practical
        programmingAssignment = new Practical("ProgrammingAssignment", "Assignment about programming");
        programmingAssignment.save();

        // Create a new practical
        documentingAssingment = new Practical("DocumentingAssignment", "Assignment about documenting");
        documentingAssingment.save();

        // Create a new invite
        programmingBobToHendrik = new Invite(programmingAssignment, bob, hendrik);
        programmingBobToHendrik.save();

        // Create a new invite
        documentingHendrikToBoB = new Invite(documentingAssingment, hendrik, bob);
        documentingHendrikToBoB.save();

        // Save all changes
        bob.save();
        hendrik.save();
        documentingAssingment.save();
        programmingAssignment.save();
    }

    @Test
    public void testCreationInvite() {
        // Test the values set in the before method
        assertEquals(documentingHendrikToBoB.getPractical().getId(), documentingAssingment.getId());
        assertEquals(documentingHendrikToBoB.getSender().getId(), bob.getId());
        assertEquals(documentingHendrikToBoB.getReceiver().getId(), hendrik.getId());
    }

    @Test
    public void testSetters() {
        // Set new values
        documentingHendrikToBoB.setId(3);
        documentingHendrikToBoB.setPractical(programmingAssignment);
        documentingHendrikToBoB.setSender(hendrik);
        documentingHendrikToBoB.setSender(bob);

        // Save new values
        documentingHendrikToBoB.save();
        bob.save();
        hendrik.save();
        programmingAssignment.save();

        // Test new values
        assertEquals(documentingHendrikToBoB.getPractical().getId(), programmingAssignment.getId());
        assertEquals(documentingHendrikToBoB.getSender().getId(), hendrik.getId());
        assertEquals(documentingHendrikToBoB.getReceiver().getId(), bob.getId());
    }
}
