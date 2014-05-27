package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import play.Logger;
import play.db.ebean.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.*;

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
    @JoinColumn(name = "practicalId")
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
    @ManyToMany(targetEntity= User.class, mappedBy = "practicalGroups", cascade = CascadeType.ALL)
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
        String sql = "select distinct t0.id c0, t0.practicalId c1, t0.ownerId c2 " +
                "from practical_group t0 " +
                "left join user_practical_group u1z_ on u1z_.practical_group_id = t0.id  " +
                "left join user u1 on u1.id = u1z_.user_id  " +
                "where (practicalId = :prctlId  " +
                    "and ((u1.id is not null  " +
                    "and u1.id = :groupMemberId1 )  " +
                        "or ownerId = :groupMemberId2 ) )";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("prctlId", practical.getId());
        sqlQuery.setParameter("groupMemberId1", groupMember.getId());
        sqlQuery.setParameter("groupMemberId2", groupMember.getId());
        SqlRow sqlRow = sqlQuery.findUnique();
        if(sqlRow == null) {
            return null;
        }
        return PracticalGroup.findById(sqlRow.getLong("c0"));
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
     * Getter for Id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for practical
     * @return practical
     */
    public Practical getPractical() {
        return practical;
    }

    /**
     * Setter for practical
     * @param practical to set
     */
    public void setPractical(Practical practical) {
        this.practical = practical;
    }

    /**
     * Getter of owner
     * @return owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Setter of owner
     * @param owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
