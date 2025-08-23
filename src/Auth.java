import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;

public class Auth {
    private static final String MASTER_FILE = "master.key";
    private static SecretKey secretKey; // AES key derived from master password

    // Derive AES key from password + salt
    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Create master password
    public static void setupMaster(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        secretKey = deriveKey(password, salt);

        // Store salt + hash of password
        String hash = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String content = Base64.getEncoder().encodeToString(salt) + ":" + hash;

        Files.write(Paths.get(MASTER_FILE), content.getBytes(), StandardOpenOption.CREATE);
        System.out.println("✅ Master password set successfully!");
    }

    // Verify entered master password
    public static boolean verifyMaster(String password) throws Exception {
        if (!Files.exists(Paths.get(MASTER_FILE))) return false;

        List<String> lines = Files.readAllLines(Paths.get(MASTER_FILE));
        String[] parts = lines.get(0).split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String storedHash = parts[1];

        SecretKey testKey = deriveKey(password, salt);
        String testHash = Base64.getEncoder().encodeToString(testKey.getEncoded());

        if (storedHash.equals(testHash)) {
            secretKey = testKey;
            return true;
        }
        return false;
    }

    // Change master password
    public static void changeMaster(String newPassword) throws Exception {
        setupMaster(newPassword);
        System.out.println("🔑 Master password changed successfully!");
    }

    // Return AES key for Vault
    public static SecretKey getSecretKey() {
        return secretKey;
    }
}
