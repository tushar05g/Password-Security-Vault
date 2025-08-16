import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

public class Vault {
    private static final String FILE_NAME = "vault.txt";
    private static final String SECRET_KEY = "1234567890123456"; // 16-char key
    private static final SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

    // Encrypt text
    private static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    // Decrypt text
    private static String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
    }

    // Add password entry
    public static void addPassword(String account, String password) throws Exception {
        String encrypted = encrypt(account + ":" + password);
        Files.write(Paths.get(FILE_NAME), (encrypted + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println("Password saved successfully!");
    }

    // View all passwords
    public static void viewPasswords() throws Exception {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            System.out.println("Vault is empty!");
            return;
        }
        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        for (String line : lines) {
            System.out.println(decrypt(line));
        }
    }

    // Delete password by account name
    public static void deletePassword(String accountName) throws Exception {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            System.out.println("Vault is empty!");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String line : lines) {
            String decrypted = decrypt(line);
            if (decrypted.toLowerCase().startsWith(accountName.toLowerCase() + ":")) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        Files.write(Paths.get(FILE_NAME), updatedLines);
        if (deleted) {
            System.out.println("Password deleted successfully for " + accountName);
        } else {
            System.out.println("Account not found: " + accountName);
        }
    }

    // Search for a specific account
    public static void searchPassword(String accountName) throws Exception {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            System.out.println("Vault is empty!");
            return;
        }
        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        boolean found = false;

        for (String line : lines) {
            String decrypted = decrypt(line);
            if (decrypted.toLowerCase().startsWith(accountName.toLowerCase() + ":")) {
                System.out.println("Found -> " + decrypted);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No account found with name: " + accountName);
        }
    }

    // Update password for a given account
    public static void updatePassword(String accountName, String newPassword) throws Exception {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            System.out.println("Vault is empty!");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String line : lines) {
            String decrypted = decrypt(line);
            if (decrypted.toLowerCase().startsWith(accountName.toLowerCase() + ":")) {
                String updatedEntry = encrypt(accountName + ":" + newPassword);
                updatedLines.add(updatedEntry);
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        Files.write(Paths.get(FILE_NAME), updatedLines);
        if (updated) {
            System.out.println("Password updated successfully for " + accountName);
        } else {
            System.out.println("Account not found: " + accountName);
        }
    }
}
