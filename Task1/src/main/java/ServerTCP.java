import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerTCP {

    public static void main(String[] args) {
        int serverPort = 7777; // The port number the server will listen on
        // Initialize a shared blockchain instance with an empty list of blocks
        BlockChain blockChain = new BlockChain(new ArrayList<Block>(), "", 0);

        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("Server is running...");

            while (true) {
                System.out.println("Waiting for a client...");
                try (Socket clientSocket = listenSocket.accept()) { // Accept incoming client connections
                    System.out.println("New client connected.");

                    // Set up "in" to read client input
                    Scanner in = new Scanner(clientSocket.getInputStream());
                    // Set up "out" to send responses to the client
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);

                    // Delegate client interaction to the handleClient method
                    handleClient(in, out, blockChain);
                } catch (IOException e) {
                    // Handle client-specific connection errors
                    System.out.println("Client connection error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Handle errors related to starting or running the server
            System.out.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Handles interaction with a single client.
     *
     * @param in         Scanner to read client input.
     * @param out        PrintWriter to send responses to the client.
     * @param blockChain Shared instance of the blockchain.
     */
    private static void handleClient(Scanner in, PrintWriter out, BlockChain blockChain) {
        try {
            // Continuously read input from the client
            while (in.hasNextLine()) {
                System.out.println("We have a visitor"); // Indicate a client request is being processed

                // Read the JSON request message from the client
                String data = in.nextLine();
                System.out.println("THE JSON REQUEST MESSAGE IS SHOWN HERE\n" + data);

                // Parse the client's JSON request into a RequestMessage object
                RequestMessage requestMessage = RequestMessage.fromJson(data);

                // Process the request and generate a response
                String response = blockChain.response(requestMessage, blockChain);

                // Wrap the response in a ResponseMessage object and convert it to JSON
                ResponseMessage responseMessage = new ResponseMessage(response);
                String responseJson = responseMessage.toJson();

                // Display the JSON response being sent to the client
                System.out.println("THE JSON RESPONSE MESSAGE IS SHOWN HERE\n" + responseJson);

                // Send the JSON response back to the client
                out.println(responseJson);

                // Log the number of blocks in the blockchain for debugging
                System.out.println("Number of Blocks on Chain == " + blockChain.getChainSize());
            }
        } catch (Exception e) {
            // Handle exceptions that occur during request processing
            System.out.println("Error processing client request: " + e.getMessage());
        } finally {
            // Log when a client disconnects
            System.out.println("Client disconnected.");
        }
    }
}
