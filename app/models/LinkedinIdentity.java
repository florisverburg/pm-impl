package models;

import com.avaje.ebean.Ebean;
import helpers.LinkedinConnection;
import play.data.validation.*;

import javax.persistence.*;

/**
 * Created by Freek on 15/05/14.
 * This includes the Linkedin OAuth 2 identity authentication method
 */
@Entity
@DiscriminatorValue("Linkedin")
@SuppressWarnings("serial")
public class LinkedinIdentity extends Identity {

    /**
     * Identifier of the identity
     * For the Linkedin Identity the identifier is the personId
     */
    @Column(name = "identifier")
    @Constraints.Required
    @Constraints.Email
    private String personId;

    /**
     * Gets person ID.
     * @return The person id
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Sets the person ID.
     * @param personId The person id
     */
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    /**
     * Create new Linkedin identity which is linked to a user
     * @param user The user where it is linked to
     * @param personId The person id of linkedin
     */
    public LinkedinIdentity(User user, String personId) {
        this.user = user;
        this.personId = personId;
    }

    /**
     * Search for an Linkedin identity by his linkedin person id
     * @param personId The linkedin person id
     * @return The identity if found else null
     */
    public static LinkedinIdentity byPersonId(String personId) {
        return Ebean.find(LinkedinIdentity.class)
                .where().eq("personId", personId)
                .findUnique();
    }

    /**
     * Authenticate using a Linkedin connection, where the user gets created if it doesn't exists
     * @param linkedinConnection The linkedin connection
     * @return The identity linked to the Linkedin user
     */
    public static LinkedinIdentity authenticate(LinkedinConnection linkedinConnection) {
        // First check if the user already exists
        LinkedinIdentity identity = byPersonId(linkedinConnection.getPersonId());

        // Create and save if the user doesn't exist
        if(identity == null) {
            User user = linkedinConnection.createUser();
            user.save();

            identity = linkedinConnection.createIdentity(user);
            identity.save();
        }

        return identity;
    }
}
