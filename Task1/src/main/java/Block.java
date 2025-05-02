import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * The Block class represents a single block in a blockchain.
 * It includes properties like index, timestamp, data, difficulty, nonce,
 * and the hash of the previous block.
 */
public class Block {

    private int index; // Index of the block in the blockchain
    private java.sql.Timestamp timestamp; // Timestamp of when the block was created
    private java.lang.String data; // Transaction or other data stored in the block
    private int difficulty; // Mining difficulty for the block
    private java.math.BigInteger nonce = BigInteger.ZERO; // Nonce used for proof of work
    private java.lang.String previousHash = ""; // Hash of the previous block in the chain

    /**
     * Constructor to initialize a Block object.
     *
     * @param index      Index of the block.
     * @param timestamp  Timestamp of block creation.
     * @param data       Data stored in the block.
     * @param difficulty Difficulty level for mining.
     */
    public Block(int index, java.sql.Timestamp timestamp, java.lang.String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    // Getters for the block's properties
    public java.lang.String getData() { return data; }
    public int getDifficulty() { return difficulty; }
    public int getIndex() { return index; }
    public java.math.BigInteger getNonce() { return nonce; }
    public java.lang.String getPreviousHash() { return previousHash; }
    public java.sql.Timestamp getTimestamp() { return timestamp; }

    // Setters for the block's properties
    public void setData(java.lang.String data) { this.data = data; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setIndex(int index) { this.index = index; }
    public void setPreviousHash(java.lang.String previousHash) { this.previousHash = previousHash; }
    public void setTimestamp(java.sql.Timestamp timestamp) { this.timestamp = timestamp; }

    /**
     * Calculates the hash of the block by concatenating its properties and
     * hashing them using the SHA-256 algorithm.
     *
     * @return The calculated hash as a hexadecimal string.
     */
    public java.lang.String calculateHash() {
        try {
            // Create a SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Concatenate the block's properties to form the input string
            String input = index + timestamp.toString() + data + difficulty + previousHash + nonce;

            // Generate the hash as a byte array
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

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
        } catch (Exception e) {
            throw new RuntimeException(e); // Handle exceptions
        }
    }

    /**
     * Performs the proof-of-work algorithm by finding a hash that starts with
     * a specific number of leading zeros (determined by the difficulty level).
     *
     * @return The valid hash that satisfies the proof-of-work condition.
     */
    public java.lang.String proofOfWork() {
        // Generate the target string with leading zeros
        String target = String.format("%0" + difficulty + "d", 0);

        // Continuously increment the nonce and calculate the hash
        while (true) {
            String hash = calculateHash();

            // Check if the hash meets the target criteria
            if (hash.startsWith(target)) {
                return calculateHash(); // Return the valid hash
            }
            nonce = nonce.add(BigInteger.ONE); // Increment the nonce
        }
    }

    /**
     * Converts the Block object to a JSON string representation using Gson.
     *
     * @return The JSON string representation of the block.
     */
    public java.lang.String toString() {
        Gson gson = new Gson();
        return gson.toJson(this); // Serialize the block as a JSON string
    }
}
