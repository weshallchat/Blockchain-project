import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class VerifyingServerTCP {

    public static void main(String[] args) {
        int serverPort = 7777; // Port number for the server to listen on
        BlockChain blockChain = new BlockChain(new ArrayList<>(), "", 0); // Shared blockchain instance to be accessed by all clients

        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("Server is running..."); // Log message indicating server startup
            var threadPool = Executors.newCachedThreadPool(); // Create a cached thread pool for handling multiple clients

            while (true) {
                System.out.println("Waiting for a client..."); // Log message indicating the server is waiting for client connections
                Socket clientSocket = listenSocket.accept(); // Accept incoming client connections
                System.out.println("New client connected."); // Log message for a new client connection
                threadPool.execute(() -> handleClient(clientSocket, blockChain)); // Handle each client request in a new thread
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage()); // Log any server errors
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    // Method to handle communication with a single client
    private static void handleClient(Socket clientSocket, BlockChain blockChain) {
        try (Scanner in = new Scanner(clientSocket.getInputStream());
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true)) {

            // Loop to read client messages until connection is closed
            while (in.hasNextLine()) {
                String data = in.nextLine(); // Read the incoming data (request) from the client
                System.out.println("THE JSON REQUEST MESSAGE IS SHOWN HERE\n" + data); // Log the received request message
                String responseJson; // Variable to store the JSON response

                try {
                    // Parse the received JSON request into a RequestMessage object
                    RequestMessage requestMessage = RequestMessage.fromJson(data);

                    // Verify the ID and signature of the request
                    SigningClientTCP signingClientTCP = new SigningClientTCP();
                    MessageVerify messageVerify = new MessageVerify(requestMessage.getE(), requestMessage.getN());
                    System.out.println("Client's public key: "+requestMessage.getE().add(requestMessage.getN())); // Log client's public key
                    // Calculate the hash of the request for signature verification
                    String concatHash = requestMessage.getID()+(requestMessage.getE().add(requestMessage.getN())).toString()+requestMessage.getChoice()+requestMessage.getCommand();
                    String hash = signingClientTCP.calculateHash(concatHash); // Generate the hash to be verified

                    // Check the validity of the ID
                    if (!signingClientTCP.calculateID(requestMessage.getE(), requestMessage.getN()).equals(requestMessage.getID())) {
                        // Invalid ID, send an error response to the client
                        String errorResponse = new ResponseMessage("Invalid ID").toJson();
                        System.out.println("Invalid ID."); // Log invalid ID error
                        out.println(errorResponse); // Send the error response to the client
                        return;
                    }

                    // Verify the signature of the request
                    if (!messageVerify.verify(hash, requestMessage.getSignature())) {
                        // Invalid signature, send an error response to the client
                        String errorResponse = new ResponseMessage("Invalid signature").toJson();
                        System.out.println("Invalid signature."); // Log invalid signature error
                        out.println(errorResponse); // Send the error response to the client
                        return;
                    } else {
                        System.out.println("Signature verified."); // Log successful signature verification
                    }

                    // If both the ID and signature are valid, process the request
                    String response = blockChain.response(requestMessage.getChoice(), requestMessage.getCommand(), blockChain); // Generate a response based on the blockchain
                    ResponseMessage responseMessage = new ResponseMessage(response); // Create a ResponseMessage object with the generated response
                    responseJson = responseMessage.toJson(); // Convert the response message to a JSON string
                    out.println(responseJson); // Send the response to the client

                } catch (Exception e) {
                    // Catch any exceptions, generate an error response and send it to the client
                    responseJson = new ResponseMessage("Error processing request: " + e.getMessage()).toJson();
                    e.printStackTrace(); // Print stack trace for debugging
                }

                // Log the JSON response message and send it to the client
                System.out.println("THE JSON RESPONSE MESSAGE IS SHOWN HERE:\n" + responseJson);
                out.println(responseJson); // Send the response back to the client
                System.out.println("Number of Blocks on Chain: " + blockChain.getChainSize()); // Log the number of blocks on the blockchain
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage()); // Log any client-related errors
        } finally {
            try {
                clientSocket.close(); // Close the client socket
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage()); // Log any errors while closing the socket
            }
            System.out.println("Client disconnected."); // Log client disconnection
        }
    }
}
