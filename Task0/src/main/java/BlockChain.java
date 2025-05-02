import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.Timestamp;
import java.util.ArrayList;

public class BlockChain {
    private ArrayList<Block> blocks; // List of blocks in the blockchain
    private String chainHash; // Hash of the entire blockchain
    private int hps; // Hashes per second (performance metric)

    // Constructor for initializing the blockchain with a genesis block
    public BlockChain(ArrayList<Block> blocks, String chainHash, int hps) {
        this.blocks = new ArrayList<Block>(); // Initializes the list of blocks
        this.chainHash = ""; // Initializes the chain hash
        this.hps = 0; // Initializes hashes per second to 0

        // Create and add the genesis block (first block in the chain)
        Block genesisBlock = new Block(0, new java.sql.Timestamp(System.currentTimeMillis()), "Genesis", 2);
        genesisBlock.setPreviousHash(""); // No previous hash for genesis block
        genesisBlock.proofOfWork(); // Perform proof of work for genesis block
        this.blocks.add(genesisBlock); // Add genesis block to the blockchain
        this.chainHash = genesisBlock.calculateHash(); // Set chain hash to genesis block's hash
    }

    // Adds a new block to the blockchain after computing its proof of work
    public void addBlock(Block newBlock) {
        Block previousBlock = blocks.get(blocks.size() - 1); // Get the last block in the chain
        newBlock.setPreviousHash(previousBlock.calculateHash()); // Set previous block's hash
        newBlock.proofOfWork(); // Perform proof of work for the new block
        chainHash = newBlock.calculateHash(); // Update the chain hash with the new block's hash
        blocks.add(newBlock); // Add the new block to the blockchain
    }

    // Computes hashes per second by hashing a fixed string repeatedly
    public void computeHashesPerSecond() {
        String hashString = "oooooooo"; // String to be hashed
        long start = System.currentTimeMillis(); // Start time for performance measurement

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // SHA-256 hashing algorithm
            for (int i = 0; i <= 2000000; i++) {
                md.update(hashString.getBytes()); // Update the hash with the string's bytes
                md.digest(); // Compute the hash (but discard it)
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }

        long end = System.currentTimeMillis(); // End time for performance measurement
        double duration = (end - start) / 1000.0; // Calculate the duration in seconds
        hps = (int) (2000000 / duration); // Calculate hashes per second
    }

    // Repairs the blockchain by recalculating proofs of work for all blocks
    public void repairChain() {
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            currentBlock.proofOfWork(); // Perform proof of work for the current block
            if (i < blocks.size() - 1) {
                Block nextBlock = blocks.get(i + 1);
                nextBlock.setPreviousHash(currentBlock.calculateHash()); // Update next block's previous hash
            }
        }
        this.chainHash = blocks.get(blocks.size() - 1).calculateHash(); // Update chain hash
    }

    // Getter method to retrieve a block by its index
    public Block getBlock(int i) {
        return blocks.get(i);
    }

    // Getter method to retrieve the most recent block
    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    // Getter method to retrieve the current blockchain's hash
    public java.lang.String getChainHash() {
        return this.chainHash;
    }

    // Getter method to retrieve the size of the blockchain
    public int getChainSize() {
        return blocks.size();
    }

    // Getter method to retrieve the hashes per second metric
    public int getHashesPerSecond() {
        return this.hps;
    }

    // Getter method to retrieve the current timestamp
    public java.sql.Timestamp getTime() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Calculates the total difficulty of all blocks in the chain
    public int getTotalDifficulty() {
        int difficulty = 0;
        for (Block block : blocks) {
            difficulty += block.getDifficulty(); // Sum up the difficulty of each block
        }
        return difficulty;
    }

    // Estimates the total number of hashes required for the entire blockchain
    public double getExpectedHashes() {
        double total = 0.0;
        for (Block block : blocks) {
            total += Math.pow(16, block.getDifficulty()); // Calculate expected hashes based on difficulty
        }
        return total;
    }

    // Verifies the integrity of the blockchain by checking each block's hash and proof of work
    public java.lang.String isChainValid() {
        if (blocks.size() == 1) { // Only the genesis block exists
            Block genesisBlock = blocks.get(0);
            String genesisHash = genesisBlock.calculateHash();
            String requiredPrefix = "0".repeat(genesisBlock.getDifficulty()); // Corrected prefix generation
            if (!genesisHash.startsWith(requiredPrefix)) {
                return "Chain Verification: FALSE\nGenesis block's proof of work is invalid";
            }
            if (!genesisHash.equals(this.chainHash)) {
                return "Chain Verification: FALSE\nGenesis block's hash does not match the Chain hash";
            }
        }

        // Check each subsequent block in the chain
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);
            if (!currentBlock.getPreviousHash().equals(previousBlock.calculateHash())) {
                return "Chain Verification: FALSE\nPrevious block's hash does not match the Chain hash";
            }
            String currentRequiredPrefix = "0".repeat(currentBlock.getDifficulty()); // Corrected prefix generation
            if (!currentBlock.calculateHash().startsWith(currentRequiredPrefix)) {
                return "Chain Verification: FALSE\nImproper hash on node " + (i - 1) + " Does not begin with " + currentRequiredPrefix;
            }
        }

        Block lastBlock = blocks.get(blocks.size() - 1);
        if (!chainHash.equals(lastBlock.calculateHash())) {
            return "Chain Verification: FALSE\nChain's hash does not match the last block's hash";
        }
        return "True"; // Blockchain is valid
    }

    // Converts the entire blockchain to a JSON string representation
    public java.lang.String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{\"ds_chain\" : [");
        for (Block b : blocks) {
            str.append(b.toString()).append("\n"); // Append each block's string representation
        }
        str.append("], \"chainHash\":\"").append(chainHash).append("\"}");
        return str.toString();
    }

    // Main method for running a simple blockchain interface
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Input reader
        BlockChain blockChain = new BlockChain(new ArrayList<Block>(), "", 0); // Initialize blockchain
        blockChain.computeHashesPerSecond(); // Compute hashes per second

        try {
            // Loop for user input to interact with the blockchain
            while (true) {
                // Display menu of options
                System.out.println("""
                        0. View basic blockchain status.
                        1. Add a transaction to the blockchain.
                        2. Verify the blockchain.
                        3. View the blockchain.
                        4. Corrupt the chain.
                        5. Hide the corruption by repairing the chain.
                        6. Exit""");
                System.out.println("Enter choice:");
                String choice = br.readLine();
                switch (choice) {
                    case "0":
                        // Display basic blockchain status
                        System.out.println("Current size of chain: " + blockChain.getChainSize());
                        System.out.println("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
                        System.out.println("Total difficulty for all blocks: " + blockChain.getTotalDifficulty());
                        System.out.println("Experimented with " + blockChain.getChainSize() + " hashes");
                        System.out.println("Approximate hashes per second on this machine: " + blockChain.getHashesPerSecond());
                        System.out.println("Expected total hashes required for the whole chain: " + blockChain.getExpectedHashes());
                        System.out.println("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
                        System.out.println("Chain hash: " + blockChain.getChainHash());
                        break;
                    case "1":
                        // Add a new transaction to the blockchain
                        System.out.println("Enter difficulty > 1");
                        String difficulty = br.readLine();
                        System.out.println("Enter a transaction");
                        String transaction = br.readLine();
                        Block block = new Block(blockChain.getChainSize(), new java.sql.Timestamp(System.currentTimeMillis()), transaction, Integer.parseInt(difficulty));
                        long startTime = System.currentTimeMillis();
                        blockChain.addBlock(block);
                        long endTime = System.currentTimeMillis();
                        System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                        break;
                    case "2":
                        // Verify the integrity of the blockchain
                        long startTime2 = System.currentTimeMillis();
                        System.out.println(blockChain.isChainValid());
                        long endTime2 = System.currentTimeMillis();
                        System.out.println("Total execution time required to verify the chain was " + (endTime2 - startTime2) + " milliseconds");
                        break;
                    case "3":
                        // View the entire blockchain
                        System.out.println("View the Blockchain");
                        System.out.println(blockChain.toString());
                        break;
                    case "4":
                        // Corrupt the blockchain by modifying a block's data
                        System.out.println("Corrupt the chain");
                        System.out.println("Enter block ID of block to corrupt");
                        int id = Integer.parseInt(br.readLine());
                        System.out.println("Enter new data for block " + id);
                        String data = br.readLine();
                        blockChain.getBlock(id).setData(data); // Modify block's data
                        break;
                    case "5":
                        // Repair the blockchain to fix any corruption
                        blockChain.repairChain();
                        System.out.println("Repair complete.");
                        break;
                    case "6":
                        // Exit the program
                        System.out.println("Exit");
                        System.exit(0);
                        break;
                    default:
                        // Handle invalid menu option
                        System.out.println("Invalid choice! Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions
        }
    }
}