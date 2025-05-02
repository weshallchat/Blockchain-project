import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/** ShortMessageVerify.java provides capabilities to verify
 *  very short messages. These messages are 4 hex digits.
 *  ShortMessageVerify has two private members: RSA e and n.
 *  These are all very small java BigIntegers. ShortMessageVerify is
 *  only used for instructional purposes.
 *  For verification: the object is constructed with keys (e and n). The verify
 *  method is called with two parameters - the string to be checked and the
 *  evidence that this string was indeed manipulated by code with access to the
 *  private key d.
 *  The message that is signed or verified is 4 hex digits.
 *  The signature is represented by a base 10 integer.
 */


public class MessageVerify {

    private BigInteger e,n;

    /** For verifying, a SignOrVerify object may be constructed
     *   with a RSA's e and n. Only e and n are used for signature verification.
     */
    public MessageVerify(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    /**
     * Verifying proceeds as follows:
     * 1) Decrypt the encryptedHash to compute a decryptedHash
     * 2) Hash the messageToCheck using SHA-256 (be sure to handle
     *    the extra byte as described in the signing method.)
     * 3) If this new hash is equal to the decryptedHash, return true else false.
     *
     * @param messageToCheck  a normal string (4 hex digits) that needs to be verified.
     * @param encryptedHashStr integer string - possible evidence attesting to its origin.
     * @return true or false depending on whether the verification was a success
     */
    public boolean verify(String messageToCheck, String encryptedHashStr)throws Exception  {

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // System.out.println(decryptedHash);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes(StandardCharsets.UTF_8);

        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);

        // messageToCheckDigest is a full SHA-256 digest
        // take two bytes from SHA-256 and add a zero byte
        byte[] messageDigest = new byte[messageToCheckDigest.length + 1];
        messageDigest[0] = 0;
        System.arraycopy(messageToCheckDigest, 0, messageDigest, 1, messageToCheckDigest.length);

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(messageDigest);

        // System.out.println(bigIntegerToCheck);

        // inform the client on how the two compare
        if(bigIntegerToCheck.compareTo(decryptedHash) == 0) {

            return true;
        }
        else {
            return false;
        }
    }
}