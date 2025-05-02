import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

/**
 * MessageSign.java provides capabilities to sign messages of any length.
 * MessageSign has three private members: RSA e, d, and n.
 * These are java BigIntegers. MessageSign is used for instructional purposes.
 * For signing: the MessageSign object is constructed with RSA
 * keys (e, d, n). These keys are not created here but are passed in by the caller.
 * Then, a caller can sign a message - the string returned by the sign
 * method is evidence that the signer has the associated private key.
 * The signature is represented by a base 10 integer.
 */
public class MessageSign {

    private BigInteger  d, n;

    /** A MessageSign object may be constructed with RSA's e, d, and n.
     *  The holder of the private key (the signer) would call this
     *  constructor. Only d and n are used for signing.
     */
    public MessageSign(BigInteger d, BigInteger n) {
        this.d = d;
        this.n = n;
    }

    /**
     * Signing proceeds as follows:
     * 1) Get the bytes from the string to be signed.
     * 2) Compute a SHA-256 digest of these bytes.
     * 3) Copy these bytes into a byte array that is one byte longer than needed.
     *    The resulting byte array has its extra byte set to zero. This is because
     *    RSA works only on positive numbers. The most significant byte (in the
     *    new byte array) is the 0'th byte. It must be set to zero.
     * 4) Create a BigInteger from the byte array.
     * 5) Encrypt the BigInteger with RSA d and n.
     * 6) Return to the caller a String representation of this BigInteger.
     * @param message a string to be signed
     * @return a string representing a big integer - the encrypted hash.
     */
    public String sign(String message) throws Exception {
        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // Add a 0 byte as the most significant byte to ensure positivity
        byte[] messageDigest = new byte[bigDigest.length + 1];
        messageDigest[0] = 0; // most significant set to 0
        System.arraycopy(bigDigest, 0, messageDigest, 1, bigDigest.length);

        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);

        // return this as a big integer string
        return c.toString();
    }
}
