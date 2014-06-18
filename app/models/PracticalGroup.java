package models;

import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.eq;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 * Class for the model representation of the database table practicalgroup
 */
@Entity
@SuppressWarnings("serial")
public class PracticalGroup extends Model {

    /**
     * primary key of the practicalgroup
     */
    @Id
    private long id;

    /**
     * Many-to-one relationship of the practicalgroup and the practical
     */
    @ManyToOne
    private Practical practical;

    /**
     * Many-to-one relationship of the practicalgroup and the owner of a group
     */
    @ManyToOne
    @JoinColumn(name = "ownerId")
    private User owner;

    /**
     * Many-to-many relationship of the practicalgroups and users
     */
    @ManyToMany(mappedBy = "practicalGroups", cascade=CascadeType.ALL)
    List<User> groupMembers = new ArrayList<User>();

    /**
     * Finder defined for the practicalgroup
     */
    public static Model.Finder<Long, PracticalGroup> find =
            new Model.Finder<Long, PracticalGroup>(Long.class, PracticalGroup.class);

    /**
     * Constructor of practical group
     * @param practical to which this group belongs to
     * @param owner of the new group
     */
    public PracticalGroup(Practical practical, User owner) {
        this.practical = practical;
        this.owner = owner;
        this.groupMembers.add(owner);
    }

    /**
     * function to find a practical group by it's id
     * @param id of the practical group to be found
     * @return practical group that was found
     */
    public static PracticalGroup findById(long id) {
        return find.where().eq("id", id).findUnique();
    }

    /**
     * A method to find a practical group using a given practical and a given user
     * @param practical of the practical group
     * @param groupMember of the practical group
     * @return practical group that was sought after
     */
    public static PracticalGroup findWithPracticalAndUser(Practical practical, User groupMember) {
        return find
                .where()
                .and(
                        eq("practical.id", practical.getId()),
                        eq("groupMembers.id", groupMember.getId())
                )
                .findUnique();
    }

    /**
     * Method to leave a group
     * @param user that wants to leave the group
     */
    public void leaveGroup(User user) {
        if(this.getGroupMembers().size() == 1) {
            return;
        }
        // Remove user from group
        this.removeGroupMember(user);
        this.save();
        this.refresh();

        // If user is the owner there needs to be a new owner,
        // so the next first member from the group will be the new owner
        if(this.getOwner().equals(user)) {
            this.setOwner(this.getGroupMembers().listIterator().next());
            this.save();
        }
        // Create a new practical group for the removed user
        PracticalGroup newPracticalGroup = new PracticalGroup(practical, user);
        newPracticalGroup.save();

        // Reset the states of the invites to Rejected
        Invite.rejectOtherInvitesUser(user, practical, true, Invite.State.Accepted);
    }

    /**
     * Getter for groupMembers
     * @return groupMembers
     */
    public List<User> getGroupMembers() {
        return groupMembers;
    }

    /** Setter for users
     * @param groupMembers to set
     */
    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     * Add user to list
     * @param groupMember to add
     */
    public void addGroupMember(User groupMember) {
        groupMembers.add(groupMember);
    }

    /**
     * Remove user from list
     * @param groupMember to remove
     */
    public void removeGroupMember(User groupMember) {
        groupMembers.remove(groupMember);
    }

    /**
     * gets the identifier of the practical group
     * @return The identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the practical where the group is from
     * @return The practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Sets the practical where the group is from
     * @param practical The practical
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }

    /**
     * Gets the owner of the practical group of
     * @return The owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the practical group
     * @param owner The user that is the new owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
