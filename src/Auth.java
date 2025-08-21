import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;

public class Auth {
    private static final String MASTER_FILE = "master.key";

    // Generate PBKDF2 hash
    private static String hashPassword(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // Create master password
    public static void setupMaster(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        String hash = hashPassword(password, salt);
        String content = Base64.getEncoder().encodeToString(salt) + ":" + hash;

        Files.write(Paths.get(MASTER_FILE), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("✅ Master password set successfully!");
    }

    // Verify entered password
    public static boolean verifyMaster(String password) throws Exception {
        if (!Files.exists(Paths.get(MASTER_FILE))) {
            return false;
        }

        List<String> lines = Files.readAllLines(Paths.get(MASTER_FILE));
        if (lines.isEmpty()) return false;

        String[] parts = lines.get(0).split(":");
        if (parts.length != 2) return false;

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String storedHash = parts[1];

        String enteredHash = hashPassword(password, salt);
        return storedHash.equals(enteredHash);
    }

    // Change master password
    public static void changeMaster(String newPassword) throws Exception {
        setupMaster(newPassword);
        System.out.println("🔑 Master password changed successfully!");
    }
}
