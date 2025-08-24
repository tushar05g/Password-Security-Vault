import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";
    private static final String KEY_FILE = "vault.key";

    // Generate AES Key (only once)
    private static SecretKey getKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (!keyFile.exists()) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey key = keyGen.generateKey();
            try (FileOutputStream fos = new FileOutputStream(KEY_FILE)) {
                fos.write(key.getEncoded());
            }
            return key;
        } else {
            byte[] keyBytes = new byte[16];
            try (FileInputStream fis = new FileInputStream(KEY_FILE)) {
                fis.read(keyBytes);
            }
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    // Encrypt
    private static String encrypt(String data) throws Exception {
        SecretKey key = getKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt
    private static String decrypt(String data) throws Exception {
        SecretKey key = getKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted);
    }

    // Add password
    public static void addPassword(String account, String password) {
        try (FileWriter fw = new FileWriter(VAULT_FILE, true)) {
            String encrypted = encrypt(account + ":" + password);
            fw.write(encrypted + "\n");
            System.out.println("✅ Password saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View all passwords
    public static void viewPasswords() {
        File vaultFile = new File(VAULT_FILE);
        if (!vaultFile.exists() || vaultFile.length() == 0) {
            System.out.println("⚠ No passwords stored yet.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            System.out.println("\n📂 Stored Passwords:");
            while ((line = br.readLine()) != null) {
                try {
                    System.out.println(decrypt(line));
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            System.out.println("⚠ No passwords found.");
        }
    }

    // Delete password by account
    public static void deletePassword(String account) {
        try {
            File inputFile = new File(VAULT_FILE);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                try {
                    String decrypted = decrypt(line);
                    if (decrypted.startsWith(account + ":")) {
                        found = true;
                        continue;
                    }
                    writer.write(line + System.lineSeparator());
                } catch (Exception ignored) {}
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Search password
    public static void searchPassword(String account) {
        File vaultFile = new File(VAULT_FILE);
        if (!vaultFile.exists() || vaultFile.length() == 0) {
            System.out.println("⚠ No passwords stored yet.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                try {
                    String decrypted = decrypt(line);
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
