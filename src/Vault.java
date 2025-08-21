import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";
    private static final String SECRET_KEY = "1234567890123456"; // 16-char key
    private static final SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

    // Encrypt
    private static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    // Decrypt
    private static String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
    }

    // Add password
    public static void addPassword(String account, String password) {
        try {
            String encrypted = encrypt(account + ":" + password);
            Files.write(Paths.get(VAULT_FILE), (encrypted + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("✅ Password saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View all
    public static void viewPasswords() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(VAULT_FILE));
            System.out.println("\nStored Passwords:");
            for (String line : lines) {
                System.out.println(decrypt(line));
            }
        } catch (Exception e) {
            System.out.println("⚠ No passwords found.");
        }
    }

    // Delete
    public static void deletePassword(String account) {
        try {
            File inputFile = new File(VAULT_FILE);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String decrypted = decrypt(line);
                if (decrypted.startsWith(account + ":")) {
                    found = true;
                    continue;
                }
                writer.write(line + System.lineSeparator());
            }
            writer.close();
            reader.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);

            if (found) {
                System.out.println("🗑 Password deleted for account: " + account);
            } else {
                System.out.println("⚠ Account not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Search
    public static void searchPassword(String account) {
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String decrypted = decrypt(line);
                if (decrypted.startsWith(account + ":")) {
                    System.out.println("🔎 Found: " + decrypted);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("⚠ No password found for account: " + account);
            }
        } catch (Exception e) {
            System.out.println("⚠ No passwords found.");
        }
    }
}
