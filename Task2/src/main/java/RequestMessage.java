import com.google.gson.Gson; // Importing Gson library for JSON serialization/deserialization
import java.math.BigInteger; // Importing BigInteger for handling large integer values

public class RequestMessage {
    private String choice; // Choice of the request (e.g., operation type)
    private String command; // Command to be executed (e.g., action or request)
    private String ID; // Unique identifier for the request
    private BigInteger e; // Public exponent for encryption (part of RSA)
    private BigInteger n; // Modulus for encryption (part of RSA)
    private String signature; // Digital signature for verifying the integrity of the message

    // Constructor to initialize a RequestMessage object with given values
    public RequestMessage(String choice, String command, String ID, BigInteger e, BigInteger n, String signature) {
        this.choice = choice;
        this.command = command;
        this.ID = ID;
        this.e = e;
        this.n = n;
        this.signature = signature;
    }

    // Getter methods to retrieve the values of the fields
    public String getChoice() { return choice; }
    public String getCommand() { return command; }
    public String getID() { return ID; }
    public BigInteger getE() { return e; }
    public BigInteger getN() { return n; }
    public String getSignature() { return signature; }

    // Setter methods to set the values of the fields
    public void setChoice(String choice) { this.choice = choice; }
    public void setCommand(String command) { this.command = command; }
    public void setID(String ID) { this.ID = ID; }
    public void setE(BigInteger e) { this.e = e; }
    public void setN(BigInteger n) { this.n = n; }
    public void setSignature(String signature) { this.signature = signature; }

    // Method to convert the current RequestMessage object to a JSON string
    public String toJson() {
        return new Gson().toJson(this); // Using Gson to serialize the object to JSON
    }

    // Static method to convert a JSON string back to a RequestMessage object
    public static RequestMessage fromJson(String json) {
        return new Gson().fromJson(json, RequestMessage.class); // Using Gson to deserialize the JSON to an object
    }
}
