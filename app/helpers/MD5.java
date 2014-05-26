package helpers;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Freek on 22/05/14.
 * MD5 encryption
 */
public final class MD5 {

    /**
     * The digester used for encoding MD5
     */
    private static MessageDigest digester;

    /**
     * The maximum length of a byte
     */
    private static final int MAX_BYTE = 0xFF;

    /**
     * Maximum of 8 bits
     */
    private static final int MAX_BITS = 0x0F;

    /**
     * Hide the default constructor
     */
    private MD5() {
        // Do nothing
    }

    /**
     * Try to get the digester
     */
    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt a string with MD5
     * @param str The string to encrypt
     * @return The MD5 encrypted string
     */
    public static String crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encrypt cannot be null or zero length");
        }

        digester.update(str.getBytes(Charset.forName("UTF-8")));
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((MAX_BYTE & hash[i]) <= MAX_BITS) {
                hexString.append("0" + Integer.toHexString((MAX_BYTE & hash[i])));
            }
            else {
                hexString.append(Integer.toHexString(MAX_BYTE & hash[i]));
            }
        }
        return hexString.toString();
    }
}
