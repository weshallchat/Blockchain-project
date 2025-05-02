import com.google.gson.Gson;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SigningClientTCP {

    public static void main(String[] args) {
        // arguments supply hostname

        Socket clientSocket = null;
        try {
            // Define the server port to connect to
            int serverPort = 7777;

            // Establish a connection to the server at localhost on the specified port
            clientSocket = new Socket("localhost", serverPort);

            // Input and output streams for communication with the server
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            RequestMessage requestMessage = null; // Request message to be sent to the server
            ResponseMessage responseMessage = null; // Response message received from the server
            String requestJson = ""; // JSON representation of the request message

            // BufferedReader for reading user input from the console
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // Main loop to interact with the user and send commands to the server
            while (true) {
                // Display the menu options to the user
                System.out.println("""
                    0. View basic blockchain status.
                    1. Add a transaction to the blockchain.
                    2. Verify the blockchain.
                    3. View the blockchain.
                    4. Corrupt the chain.
                    5. Hide the corruption by repairing the chain.
                    6. Exit""");
                System.out.println("Enter choice:");

                // Read the user's choice
                String choice = typed.readLine();
                String command = ""; // Command data for specific requests

                RSA rsa = new RSA();
                SigningClientTCP signingClientTCP = new SigningClientTCP();
                String ID = signingClientTCP.calculateID(rsa.getE(),rsa.calculateN());
                MessageSign messageSign = new MessageSign(rsa.calculateD(),rsa.calculateN());
                System.out.println("Public Key: "+rsa.getE().add(rsa.calculateN()));
                System.out.println("Private Key: "+rsa.calculateD().add(rsa.calculateN()));
                String hash = "";
                String signature = "";

                switch (choice) {
                    // Handle cases that require no additional user input
                    case "0", "2", "3", "5":
                        hash = ID+(rsa.getE().add(rsa.calculateN())).toString()+choice+command;
                        signature = messageSign.sign(signingClientTCP.calculateHash(hash));
                        // Create a request message for the selected option
                        requestMessage = new RequestMessage(choice, command, ID, rsa.getE(), rsa.calculateN(), signature);
                        requestJson = requestMessage.toJson(); // Convert the request to JSON
                        out.println(requestJson); // Send the request to the server
                        out.flush(); // Ensure data is sent immediately

                        // Read and parse the response message from the server
                        // System.out.println(in.readLine());
                        responseMessage = ResponseMessage.fromJson(in.readLine());
                        System.out.println(responseMessage.toString()); // Display the server's response
                        break;

                    // Handle adding a transaction to the blockchain
                    case "1":
                        System.out.println("Enter difficulty > 1"); // Prompt for difficulty level
                        String difficulty = typed.readLine();
                        System.out.println("Enter a transaction"); // Prompt for transaction details
                        String transaction = typed.readLine();
                        command = difficulty + "," + transaction; // Combine inputs into a single command

                        hash = ID+(rsa.getE().add(rsa.calculateN())).toString()+choice+command;
                        signature = messageSign.sign(signingClientTCP.calculateHash(hash));

                        requestMessage = new RequestMessage(choice, command, ID, rsa.getE(), rsa.calculateN(), signature); // Create the request
                        requestJson = requestMessage.toJson(); // Convert to JSON
                        out.println(requestJson); // Send to server
                        out.flush(); // Ensure data is sent

                        // System.out.println(in.readLine());
                        responseMessage = ResponseMessage.fromJson(in.readLine()); // Read server response
                        System.out.println(responseMessage.toString()); // Display the response
                        break;

                    // Handle corrupting the blockchain
                    case "4":
                        System.out.println("Corrupt the chain");
                        System.out.println("Enter block ID of block to corrupt"); // Prompt for block ID
                        String id = typed.readLine();
                        System.out.println("Enter new data for block " + id); // Prompt for new data
                        String data = typed.readLine();
                        command = id + "," + data; // Combine inputs into a single command

                        hash = ID + (rsa.getE().add(rsa.calculateN())).toString() + choice + command;
                        signature = messageSign.sign(signingClientTCP.calculateHash(hash));


                        requestMessage = new RequestMessage(choice, command, ID, rsa.getE(), rsa.calculateN(), signature); // Create the request
                        requestJson = requestMessage.toJson(); // Convert to JSON
                        out.println(requestJson); // Send to server
                        out.flush(); // Ensure data is sent

                        // System.out.println(in.readLine());
                        responseMessage = ResponseMessage.fromJson(in.readLine()); // Read server response
                        System.out.println(responseMessage.toString()); // Display the response
                        break;

                    // Exit the program
                    case "6":
                        return;

                    // Handle invalid input
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }
        } catch (IOException e) {
            // Handle exceptions related to input/output operations
            System.out.println("IO Exception:" + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // Close the client socket to release resources
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Ignore exceptions that occur during socket closure
            }
        }
    }
    public java.lang.String calculateID(BigInteger e, BigInteger n) {
        try {
            // Create a SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Concatenate the block's properties to form the input string
            String input = (e.add(n)).toString();

            // Generate the hash as a byte array
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            byte[] last20 = new byte[20];
            System.arraycopy(hashBytes, hashBytes.length-20, last20, 0, 20);

            StringBuilder hexString = new StringBuilder();
            for(byte b: last20){
                hexString.append(String.format("%02x",b));
            }
            return hexString.toString(); // Return the hash as a string
        } catch (Exception exception) {
            throw new RuntimeException(exception); // Handle exceptions
        }
    }
    public java.lang.String calculateHash(String input) {
        try {
            // Create a SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Generate the hash as a byte array
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // Ensure two-character representation
                }
                hexString.append(hex);
            }
            return hexString.toString(); // Return the hash as a string
        } catch (Exception exception) {
            throw new RuntimeException(exception); // Handle exceptions
        }
    }
}
