package vsas.utils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.annotations.Param;

import java.security.SecureRandom;

public class Encoder {
    /**Encode password String into salted hexadecimal String
     * Use this to encode password when user creating their account*/
    public static String encode(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        String saltedPassword = Hex.encodeHexString(salt) + password;
        String hashedPassword = DigestUtils.sha256Hex(saltedPassword);

        // Store the saltedPassword (hexadecimal) and hashedPassword in your database
        return Hex.encodeHexString(salt) + ":" + hashedPassword;
    }


    /**Verify the password store in database with the password user entered
     * Use this to verify the password entered by the user against the password store in database*/
    public static boolean verify(String storedPassword, String enteredPassword) {
        // Split the storedPassword into salt and hashedPassword
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            return false; // Invalid format
        }

        String storedSalt = parts[0];
        String storedHashedPassword = parts[1];

        String enteredSaltedPassword = storedSalt + enteredPassword;
        String enteredHashedPassword = DigestUtils.sha256Hex(enteredSaltedPassword);

        return enteredHashedPassword.equals(storedHashedPassword);
    }


}
