import com.google.gson.Gson;

/**
 * Represents a request message with a choice and command.
 * This class is used to communicate between the client and server by converting
 * objects to JSON and vice versa.
 */
public class RequestMessage {
    private String choice;  // The choice made by the user (menu option)
    private String command; // The command or data associated with the choice

    /**
     * Constructs a new RequestMessage with the specified choice and command.
     *
     * @param choice  The choice made by the user.
     * @param command The command or data associated with the choice.
     */
    public RequestMessage(String choice, String command) {
        this.choice = choice;
        this.command = command;
    }

    /**
     * Gets the choice.
     *
     * @return The choice made by the user.
     */
    public String getChoice() {
        return choice;
    }

    /**
     * Gets the command.
     *
     * @return The command or data associated with the choice.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the choice.
     *
     * @param choice The choice to set.
     */
    public void setChoice(String choice) {
        this.choice = choice;
    }

    /**
     * Sets the command.
     *
     * @param command The command or data to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Converts this RequestMessage object to a JSON string.
     *
     * @return A JSON representation of this object.
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Creates a RequestMessage object from a JSON string.
     *
     * @param json The JSON string representing a RequestMessage.
     * @return A RequestMessage object parsed from the JSON string.
     */
    public static RequestMessage fromJson(String json) {
        return new Gson().fromJson(json, RequestMessage.class);
    }
}
