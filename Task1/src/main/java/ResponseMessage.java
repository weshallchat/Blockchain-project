import com.google.gson.Gson;

/**
 * Represents a response message with a message string.
 * This class is used to communicate responses between the server and client
 * by converting objects to JSON and vice versa.
 */
public class ResponseMessage {
    private String message; // The response message content

    /**
     * Constructs a new ResponseMessage with the specified message.
     *
     * @param message The response message to set.
     */
    public ResponseMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the response message.
     *
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message The response message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the string representation of the response message.
     *
     * @return The response message as a string.
     */
    @Override
    public String toString() {
        return message;
    }

    /**
     * Converts this ResponseMessage object to a JSON string.
     *
     * @return A JSON representation of this object.
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Creates a ResponseMessage object from a JSON string.
     *
     * @param json The JSON string representing a ResponseMessage.
     * @return A ResponseMessage object parsed from the JSON string.
     */
    public static ResponseMessage fromJson(String json) {
        return new Gson().fromJson(json, ResponseMessage.class);
    }
}
