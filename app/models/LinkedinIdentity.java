package models;

import com.avaje.ebean.Ebean;
import play.data.validation.*;
import play.mvc.*;

import javax.persistence.*;

/**
 * Created by Freek on 15/05/14.
 * This includes the Linkedin OAuth 2 identity authentication method
 */
@Entity
@DiscriminatorValue("L")
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

    public LinkedinIdentity(User user, String personId) {
        this.user = user;
        this.personId = personId;
    }

    /**
     * Check the authentication code from linkedin
     * @param session The current session
     * @param code The code from linkedin
     * @return The identity if found else null
     */
    public static Identity authenticate(Http.Session session, String code) {
        //
        return null;
    }

    public static LinkedinIdentity byPersonId(String personId) {
        return Ebean.find(LinkedinIdentity.class)
                .where().eq("personId", personId)
                .findUnique();
    }
}
