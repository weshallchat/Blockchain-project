/* Demonstrate RSA in Java using BigIntegers */


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *  RSA Algorithm from CLR
 * 1. Select at random two large prime numbers p and q.
 * 2. Compute n by the equation n = p * q.
 * 3. Compute phi(n)=  (p - 1) * ( q - 1)
 * 4. Select a small odd integer e that is relatively prime to phi(n).
 * 5. Compute d as the multiplicative inverse of e modulo phi(n). A theorem in
 *    number theory asserts that d exists and is uniquely defined.
 * 6. Publish the pair P = (e,n) as the RSA public key.
 * 7. Keep secret the pair S = (d,n) as the RSA secret key.
 * 8. To encrypt a message M compute C = M^e (mod n)
 * 9. To decrypt a message C compute M = C^d (mod n)
 */

public class RSA {
    // Each public and private key consists of an exponent and a modulus
    BigInteger e; // e is the exponent of the public key
    BigInteger p;
    BigInteger q;
    BigInteger n;
    BigInteger d;
    public RSA() {
        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
        // To sign, say, a 256 bit hash, 2048 bits is recommended.
        Random rnd = new Random();
        this.e = new BigInteger("65537");
        this.p = new BigInteger(400, 100, rnd);
        this.q = new BigInteger(400, 100, rnd);

    }
    public BigInteger getE() {return e;}
    public void setE(BigInteger e) {this.e=e;}

    public BigInteger calculateN() {
        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);
        return n;
    }
    public BigInteger calculateD() {
        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        d = e.modInverse(phi);
        return d;
    }
    // Step 8: To encrypt a message M compute C = M^e (mod n)
    public BigInteger encryptMessage(BigInteger m) {
        return  m.modPow(e, n);
    }
    // Step 9: To decrypt a message C compute M = C^d (mod n)
    public BigInteger decryptMessage(BigInteger c) {
        return c.modPow(d, n);
    }
    // Step 10: Encrypt the string 'RSA is way cool.'
    public BigInteger encryptString(String s) {
        BigInteger m = new BigInteger(s.getBytes()); // m is the original clear text
        return m.modPow(e, n); // Do the encryption, c is the cypher text
    }
    public String decryptString(BigInteger c) {
        BigInteger clear = c.modPow(d, n); // Decrypt, clear is the resulting clear text
        return new String(clear.toByteArray()); // Decode to a string
    }
}
