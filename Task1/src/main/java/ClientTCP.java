import com.google.gson.Gson;

import java.net.*;
import java.io.*;

public class ClientTCP {

    public static void main(String args[]) {
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

                switch (choice) {
                    // Handle cases that require no additional user input
                    case "0", "2", "3", "5":
                        // Create a request message for the selected option
                        requestMessage = new RequestMessage(choice, command);
                        requestJson = requestMessage.toJson(); // Convert the request to JSON
                        out.println(requestJson); // Send the request to the server
                        out.flush(); // Ensure data is sent immediately

                        // Read and parse the response message from the server
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
                        requestMessage = new RequestMessage(choice, command); // Create the request
                        requestJson = requestMessage.toJson(); // Convert to JSON
                        out.println(requestJson); // Send to server
                        out.flush(); // Ensure data is sent

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
                        requestMessage = new RequestMessage(choice, command); // Create the request
                        requestJson = requestMessage.toJson(); // Convert to JSON
                        out.println(requestJson); // Send to server
                        out.flush(); // Ensure data is sent

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
}
