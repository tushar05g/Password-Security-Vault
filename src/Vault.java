import java.io.*;
import java.util.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";
    private static final String SECRET_KEY = "1234567890123456"; // 16-char AES key
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

    // Utility: check if a string is valid Base64
    private static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Add a new password
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
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            System.out.println("\nStored Passwords:");
            while ((line = br.readLine()) != null) {
                if (isBase64(line)) {
                    try {
                        System.out.println(decrypt(line));
                    } catch (Exception e) {
                        System.out.println("⚠ Corrupted entry skipped.");
                    }
                } else {
                    System.out.println("⚠ Skipping invalid entry: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠ No passwords found.");
        }
    }

    // Delete a password by account
    public static void deletePassword(String account) {
        try {
            File inputFile = new File(VAULT_FILE);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (isBase64(line)) {
                    try {
                        String decrypted = decrypt(line);
                        if (decrypted.startsWith(account + ":")) {
                            found = true;
                            continue; // skip writing this line
                        }
                    } catch (Exception e) {
                        System.out.println("⚠ Corrupted entry skipped during delete.");
                    }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Search password by account
    public static void searchPassword(String account) {
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (isBase64(line)) {
                    try {
                        String decrypted = decrypt(line);
                        if (decrypted.startsWith(account + ":")) {
                            System.out.println("🔎 Found: " + decrypted);
                            found = true;
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("⚠ Corrupted entry skipped during search.");
                    }
                }
            }
            if (!found) {
                System.out.println("⚠ No password found for account: " + account);
            }
        } catch (IOException e) {
            System.out.println("⚠ No passwords found.");
        }
    }
}
