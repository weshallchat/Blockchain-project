import java.security.MessageDigest;
import java.util.ArrayList;

public class BlockChain {
    private ArrayList<Block> blocks; // List to store the chain of blocks
    private String chainHash; // Hash of the latest block in the chain
    private int hps; // Hashes per second calculated by computeHashesPerSecond()

    // Constructor initializes the blockchain with a genesis block
    public BlockChain(ArrayList<Block> blocks, String chainHash, int hps) {
        this.blocks = new ArrayList<Block>();
        this.chainHash = "";
        this.hps = 0;

        // Create the genesis block
        Block genesisBlock = new Block(0, new java.sql.Timestamp(System.currentTimeMillis()), "Genesis", 2);
        genesisBlock.setPreviousHash(""); // Genesis block has no previous hash
        genesisBlock.proofOfWork(); // Perform proof-of-work for the genesis block
        this.blocks.add(genesisBlock); // Add the genesis block to the chain
        this.chainHash = genesisBlock.calculateHash(); // Set the chain hash to the genesis block's hash
    }

    // Adds a new block to the blockchain
    public void addBlock(Block newBlock) {
        Block previousBlock = blocks.get(blocks.size() - 1); // Get the last block in the chain
        newBlock.setPreviousHash(previousBlock.calculateHash()); // Set the new block's previous hash
        newBlock.proofOfWork(); // Perform proof-of-work for the new block
        chainHash = newBlock.calculateHash(); // Update the chain hash
        blocks.add(newBlock); // Add the new block to the chain
    }

    // Computes the hashes per second that can be processed
    public void computeHashesPerSecond() {
        String hashString = "oooooooo"; // String used for hash computation
        long start = System.currentTimeMillis(); // Start time for measurement
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (int i = 0; i <= 2000000; i++) {
                md.update(hashString.getBytes());
                md.digest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis(); // End time for measurement
        double duration = (end - start) / 1000.0; // Duration in seconds
        hps = (int) (2000000 / duration); // Calculate hashes per second
    }

    // Repairs the blockchain to ensure consistency
    public void repairChain() {
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            currentBlock.proofOfWork(); // Recompute proof-of-work for each block
            if (i < blocks.size() - 1) {
                Block nextBlock = blocks.get(i + 1);
                nextBlock.setPreviousHash(currentBlock.calculateHash()); // Update the next block's previous hash
            }
        }
        this.chainHash = blocks.get(blocks.size() - 1).calculateHash(); // Update the chain hash
    }

    // Getter for a block by index
    public Block getBlock(int i) {
        return blocks.get(i);
    }

    // Getter for the latest block in the chain
    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    // Getter for the chain hash
    public String getChainHash() {
        return this.chainHash;
    }

    // Getter for the size of the blockchain
    public int getChainSize() {
        return blocks.size();
    }

    // Getter for hashes per second
    public int getHashesPerSecond() {
        return this.hps;
    }

    // Getter for the current timestamp
    public java.sql.Timestamp getTime() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Calculates the total difficulty of the blockchain
    public int getTotalDifficulty() {
        int difficulty = 0;
        for (Block block : blocks) {
            difficulty += block.getDifficulty();
        }
        return difficulty;
    }

    // Calculates the total expected number of hashes required for the chain
    public double getExpectedHashes() {
        double total = 0.0;
        for (Block block : blocks) {
            total += Math.pow(16, block.getDifficulty());
        }
        return total;
    }

    // Verifies the integrity of the blockchain
    public String isChainValid() {
        if (blocks.size() == 1) { // Validate the genesis block
            Block genesisBlock = blocks.get(0);
            String genesisHash = genesisBlock.calculateHash();
            String requiredPrefix = "0".repeat(genesisBlock.getDifficulty()); // Difficulty prefix
            if (!genesisHash.startsWith(requiredPrefix)) {
                return "Chain Verification: FALSE\nGenesis block's proof of work is invalid";
            }
            if (!genesisHash.equals(this.chainHash)) {
                return "Chain Verification: FALSE\nGenesis block's hash does not match the Chain hash";
            }
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);
            if (!currentBlock.getPreviousHash().equals(previousBlock.calculateHash())) {
                return "Chain Verification: FALSE\nPrevious block's hash does not match the Chain hash";
            }
            String currentRequiredPrefix = "0".repeat(currentBlock.getDifficulty());
            if (!currentBlock.calculateHash().startsWith(currentRequiredPrefix)) {
                return "Chain Verification: FALSE\nImproper hash on node " + (i - 1) + " Does not begin with " + currentRequiredPrefix;
            }
        }

        Block lastBlock = blocks.get(blocks.size() - 1);
        if (!chainHash.equals(lastBlock.calculateHash())) {
            return "Chain Verification: FALSE\nChain's hash does not match the last block's hash";
        }
        return "True";
    }

    // Converts the blockchain to a JSON-like string
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{\"ds_chain\" : [");
        for (Block b : blocks) {
            str.append(b.toString()).append("\n");
        }
        str.append("], \"chainHash\":\"").append(chainHash).append("\"}");
        return str.toString();
    }

    // Handles various blockchain operations based on user requests
    public String response(RequestMessage requestMessage, BlockChain blockChain) {
        StringBuilder sb = new StringBuilder();
        blockChain.computeHashesPerSecond(); // Update hashes per second
        String choice = requestMessage.getChoice();
        String command = requestMessage.getCommand();
        String[] commands = command.split(",");
        long startTime = 0;
        long endTime = 0;
        try {
            switch (choice) {
                case "0": // Display blockchain information
                    sb.append("Current size of chain: ").append(blockChain.getChainSize()).append("\n");
                    sb.append("Difficulty of most recent block: ").append(blockChain.getLatestBlock().getDifficulty()).append("\n");
                    sb.append("Total difficulty for all blocks: ").append(blockChain.getTotalDifficulty()).append("\n");
                    sb.append("Experimented with ").append(blockChain.getChainSize()).append(" hashes").append("\n");
                    sb.append("Approximate hashes per second on this machine: ").append(blockChain.getHashesPerSecond()).append("\n");
                    sb.append("Expected total hashes required for the whole chain: ").append(blockChain.getExpectedHashes()).append("\n");
                    sb.append("Nonce for most recent block: ").append(blockChain.getLatestBlock().getNonce()).append("\n");
                    sb.append("Chain hash: ").append(blockChain.getChainHash());
                    break;
                case "1": // Add a new block
                    String difficulty = commands[0];
                    String transaction = commands[1];
                    Block block = new Block(blockChain.getChainSize(), new java.sql.Timestamp(System.currentTimeMillis()), transaction, Integer.parseInt(difficulty));
                    startTime = System.currentTimeMillis();
                    blockChain.addBlock(block);
                    endTime = System.currentTimeMillis();
                    sb.append("Total execution time to add this block was ").append((endTime - startTime)).append(" milliseconds");
                    break;
                case "2": // Verify the blockchain
                    startTime = System.currentTimeMillis();
                    sb.append("Verifying the entire chain").append("\n").append("Chain verification: ").append(blockChain.isChainValid()).append("\n");
                    endTime = System.currentTimeMillis();
                    sb.append("Total execution time required to verify the chain was ").append((endTime - startTime)).append(" milliseconds");
                    break;
                case "3": // View the blockchain
                    sb.append("View the Blockchain").append("\n").append(blockChain.toString());
                    break;
                case "4": // Update a block's data
                    int id = Integer.parseInt(commands[0]);
                    String data = commands[1];
                    blockChain.getBlock(id).setData(data);
                    sb.append("Block ").append(id).append(" now holds ").append(data);
                    break;
                case "5": // Repair the blockchain
                    startTime = System.currentTimeMillis();
                    blockChain.repairChain();
                    endTime = System.currentTimeMillis();
                    sb.append("Repairing the entire chain\n").append("Total execution time required to repair the chain was ").append((endTime - startTime)).append(" milliseconds");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
