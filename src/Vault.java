import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.util.Base64;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";

    // Encrypt text with AES
    private static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    // Decrypt text with AES
    private static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decoded));
    }

    // Add a new password (encrypted)
    public static void addPassword(String account, String password) {
        try (FileWriter fw = new FileWriter(VAULT_FILE, true)) {
            String encrypted = encrypt(account + ":" + password, Auth.getSecretKey());
            fw.write(encrypted + "\n");
            System.out.println("✅ Password saved securely!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View all passwords (decrypted)
    public static void viewPasswords() {
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            boolean found = false;

            System.out.println("\n📂 Stored Passwords:");
            while ((line = br.readLine()) != null) {
                try {
                    String decrypted = decrypt(line, Auth.getSecretKey());
                    System.out.println(decrypted);
                    found = true;
                } catch (Exception ignored) {
                    // skip corrupt/old lines
                }
            }

            if (!found) {
                System.out.println("⚠ No stored passwords.");
            }
        } catch (IOException e) {
            System.out.println("⚠ No stored passwords.");
        }
    }


    // Delete a password by account
    public static void deletePassword(String account) {
        File inputFile = new File(VAULT_FILE);
        File tempFile = new File("temp.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String decrypted = decrypt(line, Auth.getSecretKey());
                    if (decrypted.startsWith(account + ":")) {
                        found = true;
                        continue; // skip this entry
                    }
                    writer.write(line + System.lineSeparator());
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);

        if (found) {
            System.out.println("🗑 Password deleted for account: " + account);
        } else {
            System.out.println("⚠ Account not found!");
        }
    }

    // Search password by account
    public static void searchPassword(String account) {
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                try {
                    String decrypted = decrypt(line, Auth.getSecretKey());
                    if (decrypted.startsWith(account + ":")) {
                        System.out.println("🔎 Found: " + decrypted);
                        found = true;
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if (!found) {
                System.out.println("⚠ No password found for account: " + account);
            }
        } catch (IOException e) {
            System.out.println("⚠ No passwords found.");
        }
    }
}
