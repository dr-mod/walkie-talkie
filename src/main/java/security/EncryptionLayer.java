package security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class EncryptionLayer {

    private static final byte[] SALT = new byte[]{127, -51, 83, 79, 72, 75, -102, 32, 97, 26, 43, -36, -22, -55, -117, -41, 87, 81, -38, -29, -89, -44, 110, -32, -55, -6, 57, 81, 4, -10, -90, -49, 23, 118, -74, 16, 33, -93, -81, 105, -107, 40, -71, -127, -122, -61, -119, 42, 44, -81, 47, 47, 66, -23, 71, 43, -31, -58, -86, -125, 118, -74, 41, 44, -80, -10, 73, -77, -28, -87, 104, 109, 83, -62, 68, -77, -45, -86, -1, 20, 3, 36, 34, -94, 82, -62, -67, -6, -65, 16, -85, -94, -118, -75, -117, 31, -80, 66, -72, 31, -3, -77, 80, 23, 15, -125, 78, 58, -124, 0, 88, -123, -65, -43, -116, 88, 39, 68, 21, -2, 24, -54, -54, 44, 41, -103, -115, -115};
    public static final int IV_SIZE = 16;
    private final SecretKeySpec aesKey;

    public EncryptionLayer(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, SALT, 12288, 128);
        SecretKey tmp = factory.generateSecret(spec);
        this.aesKey = new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public InputStream secureInputStream(InputStream inputStream) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = new byte[IV_SIZE];
        int bytesRead = inputStream.read(iv);
        if (bytesRead != IV_SIZE) {
            throw new RuntimeException("Bad IV received");
        }
        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, this.aesKey, new IvParameterSpec(iv), new SecureRandom());
        return new CipherInputStream(inputStream, ci);
    }

    public OutputStream secureOutputStream(OutputStream outputStream) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] iv = generateIV();
        outputStream.write(iv);
        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, this.aesKey, new IvParameterSpec(iv), new SecureRandom());
        return new CipherOutputStream(outputStream, ci);
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
