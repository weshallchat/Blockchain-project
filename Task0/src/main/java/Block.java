import java.math.BigInteger;
import java.security.MessageDigest;

import com.google.gson.Gson;

public class Block {

    // Fields representing the properties of a block in a blockchain
    private int index; // Block index or position in the chain
    private java.sql.Timestamp timestamp; // Timestamp of block creation
    private java.lang.String data; // Data or payload stored in the block
    private int difficulty; // Mining difficulty (number of leading zeros in hash)
    private java.math.BigInteger nonce = BigInteger.ZERO; // Nonce used for Proof of Work
    private java.lang.String previousHash = ""; // Hash of the previous block in the chain

    // Constructor to initialize the block with specific values
    public Block(int index, java.sql.Timestamp timestamp, java.lang.String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    // Getters for accessing private fields
    public java.lang.String getData() { return data; }
    public int getDifficulty() { return difficulty; }
    public int getIndex() { return index; }
    public java.math.BigInteger getNonce() { return nonce; }
    public java.lang.String getPreviousHash() { return previousHash; }
    public java.sql.Timestamp getTimestamp() { return timestamp; }

    // Setters for modifying private fields
    public void setData(java.lang.String data) { this.data = data; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setIndex(int index) { this.index = index; }
    public void setPreviousHash(java.lang.String previousHash) { this.previousHash = previousHash; }
    public void setTimestamp(java.sql.Timestamp timestamp) { this.timestamp = timestamp; }

    // Method to calculate the hash of the block
    // Combines all block properties to generate a SHA-256 hash
    public java.lang.String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = index + timestamp.toString() + data + difficulty + previousHash + nonce;
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            // Convert hash bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // Ensure two-character hex representation
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // Handle exceptions during hashing
        }
    }

    // Method to perform Proof of Work (PoW) for the block
    // Finds a hash that starts with a specific number of zeros determined by the difficulty
    public java.lang.String proofOfWork() {
        String target = String.format("%0" + difficulty + "d", 0); // Target hash pattern
        while (true) {
            String hash = calculateHash();
            if (hash.startsWith(target)) { // Check if the hash meets the difficulty target
                return calculateHash(); // Return the valid hash
            }
            nonce = nonce.add(BigInteger.ONE); // Increment nonce and try again
        }
    }

    // Converts the block object to a JSON string representation using Gson
    public java.lang.String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Main method placeholder (not used in the current implementation)
    public static void main(java.lang.String[] args) {}
}
