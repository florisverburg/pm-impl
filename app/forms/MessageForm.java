package forms;

import play.data.validation.*;

/**
 * Created by Floris on 02/06/14.
 * The form to send a message
 */
public class MessageForm {

    /**
     * The message
     */
    @Constraints.Required
    private String message;

    /**
     * Gets message.
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
