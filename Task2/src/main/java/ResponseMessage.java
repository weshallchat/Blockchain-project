import com.google.gson.Gson; // Importing Gson library for JSON serialization/deserialization

public class ResponseMessage {
    private String message; // Message field that holds the response message

    // Constructor to initialize the ResponseMessage object with the given message
    public ResponseMessage(String message) {
        this.message = message;
    }

    // Getter method to retrieve the message
    public String getMessage() {
        return message;
    }

    // Setter method to set the message value
    public void setMessage(String message) {
        this.message = message;
    }

    // toString() method returns the message as a string representation of the object
    @Override
    public String toString() {
        return message;
    }

    // Method to convert the current ResponseMessage object to a JSON string
    public String toJson() {
        return new Gson().toJson(this); // Using Gson to serialize the object to JSON format
    }

    // Static method to convert a JSON string back to a ResponseMessage object
    public static ResponseMessage fromJson(String json) {
        return new Gson().fromJson(json, ResponseMessage.class); // Using Gson to deserialize the JSON to an object
    }
}
