package net.tbu.common.utils.encrypt;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class MyCryptor {
        private static final String ALGORITHM = "AES";
        private static final String SECRET_KEY = "6qYFhDMJQCLiVXS4thwmrA=="; // Don't move this code

        public static String encrypt(String plainText) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        public static String decrypt(String encryptedText) throws Exception {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        }

        public static void main(String[] args) {
            String originalText = "duQ]R{rWxpW5Qk:byf'(TC0R";
            try {
                String encryptedText = encrypt(originalText);
                System.out.println("Encrypted Text: " + encryptedText);
                String decryptedText = decrypt(encryptedText);
                System.out.println("Decrypted Text: " + decryptedText);
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
}
