import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";
    private static final String KEY_FILE = "vault.key";

    // ===== AES-256 local key management =====
    private static SecretKey loadKey() throws Exception {
        File f = new File(KEY_FILE);
        if (!f.exists()) {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            try {
                gen.init(256); // try AES-256
            } catch (Exception e) {
                gen.init(128); // fallback if policy/provider restricts 256
            }
            SecretKey key = gen.generateKey();
            try (FileOutputStream fos = new FileOutputStream(KEY_FILE)) {
                fos.write(key.getEncoded());
            }
            return key;
        } else {
            byte[] data = Files.readAllBytes(f.toPath());
            return new SecretKeySpec(data, "AES");
        }
    }

    private static String encryptLocal(String plaintext) throws Exception {
        SecretKey key = loadKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(iv) + ":" +
                Base64.getEncoder().encodeToString(ct);
    }

    private static String decryptLocal(String ivColonCtB64) throws Exception {
        String[] parts = ivColonCtB64.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Bad record format");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] ct = Base64.getDecoder().decode(parts[1]);
        SecretKey key = loadKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] pt = cipher.doFinal(ct);
        return new String(pt, StandardCharsets.UTF_8);
    }

    // ===== Public CRUD =====
    public static void addPassword(String account, String password) {
        try {
            if (account == null || account.isBlank() || password == null || password.isBlank()) {
                System.out.println("⚠ Account and password cannot be empty.");
                return;
            }
            String line = encryptLocal(account + ":" + password);
            Files.write(Paths.get(VAULT_FILE), (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("✅ Password saved!");
        } catch (Exception e) {
            System.out.println("❌ Failed to save password.");
        }
    }

    public static void viewPasswords() {
        Path p = Paths.get(VAULT_FILE);
        if (!Files.exists(p) || p.toFile().length() == 0) {
            System.out.println("⚠ Vault is empty.");
            return;
        }
        int shown = 0;
        System.out.println("\n📂 Stored Passwords:");
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    System.out.println(decryptLocal(line));
                    shown++;
                } catch (Exception ignore) { /* skip corrupt/legacy lines */ }
            }
        } catch (IOException e) {
            System.out.println("⚠ Could not read vault.");
            return;
        }
        if (shown == 0) System.out.println("⚠ Vault is empty.");
    }

    public static void deletePassword(String account) {
        Path src = Paths.get(VAULT_FILE);
        if (!Files.exists(src)) {
            System.out.println("⚠ Vault is empty.");
            return;
        }
        Path tmp = Paths.get("temp.txt");
        boolean found = false;

        try (BufferedReader br = Files.newBufferedReader(src, StandardCharsets.UTF_8);
             BufferedWriter bw = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            String line;
            while ((line = br.readLine()) != null) {
                String keep = line;
                try {
                    String pt = decryptLocal(line);
                    if (pt.startsWith(account + ":")) {
                        found = true;
                        continue; // skip (deletes)
                    }
                } catch (Exception ignore) { /* skip bad lines */ }
                bw.write(keep);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Delete failed.");
            return;
        }

        try {
            Files.deleteIfExists(src);
            Files.move(tmp, src, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.out.println("❌ Commit delete failed.");
            return;
        }

        if (found) System.out.println("🗑 Password deleted for account: " + account);
        else System.out.println("⚠ Account not found!");
    }

    public static void searchPassword(String account) {
        Path p = Paths.get(VAULT_FILE);
        if (!Files.exists(p) || p.toFile().length() == 0) {
            System.out.println("⚠ Vault is empty.");
            return;
        }
        boolean found = false;
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String pt = decryptLocal(line);
                    if (pt.startsWith(account + ":")) {
                        System.out.println("🔎 Found: " + pt);
                        found = true;
                        break;
                    }
                } catch (Exception ignore) {}
            }
        } catch (IOException e) {
            System.out.println("⚠ Could not read vault.");
            return;
        }
        if (!found) System.out.println("⚠ No password found for account: " + account);
    }

    // ===== Password Generator =====
    public static String generatePassword(int length, boolean useNumbers, boolean useSpecial) {
        if (length < 6) length = 6;
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String special = "!@#$%^&*()-_=+<>?/";

        String chars = upper + lower;
        if (useNumbers) chars += numbers;
        if (useSpecial) chars += special;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // Optional: guarantee at least one of the requested sets
        if (useNumbers) sb.append(numbers.charAt(random.nextInt(numbers.length())));
        if (useSpecial) sb.append(special.charAt(random.nextInt(special.length())));

        while (sb.length() < length) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // Shuffle
        List<Character> list = new ArrayList<>();
        for (char c : sb.toString().toCharArray()) list.add(c);
        Collections.shuffle(list, random);
        StringBuilder out = new StringBuilder();
        for (char c : list) out.append(c);
        return out.toString();
    }

    // ===== Export / Import using passphrase (portable backups) =====
    // Format: v1:<Base64(salt)>:<Base64(iv)>:<Base64(ciphertext)>
    public static int exportVault(String exportPath, String passphrase) {
        try {
            Path p = Paths.get(VAULT_FILE);
            if (!Files.exists(p) || p.toFile().length() == 0) {
                Files.write(Paths.get(exportPath), "v1:::\n".getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                return 0;
            }

            // Decrypt each entry to plaintext "account:password"
            List<String> plains = new ArrayList<>();
            try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        String pt = decryptLocal(line.trim());
                        if (!pt.isBlank()) plains.add(pt);
                    } catch (Exception ignore) {}
                }
            }
            String joined = String.join("\n", plains);

            // Encrypt with passphrase-derived key
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            SecretKey passKey = deriveKey(passphrase, salt, 256);

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, passKey, new IvParameterSpec(iv));
            byte[] ct = cipher.doFinal(joined.getBytes(StandardCharsets.UTF_8));

            String payload = "v1:" +
                    Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(iv) + ":" +
                    Base64.getEncoder().encodeToString(ct);

            Files.write(Paths.get(exportPath), (payload + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return plains.size();
        } catch (Exception e) {
            return -1;
        }
    }

    public static int importVault(String importPath, String passphrase, boolean overwrite) {
        try {
            String line = Files.readAllLines(Paths.get(importPath), StandardCharsets.UTF_8).get(0);
            if (!line.startsWith("v1:")) return -1;

            String[] parts = line.split(":");
            if (parts.length != 4) return -1;

            byte[] salt = parts[1].isEmpty() ? new byte[16] : Base64.getDecoder().decode(parts[1]);
            byte[] iv = parts[2].isEmpty() ? new byte[16] : Base64.getDecoder().decode(parts[2]);
            byte[] ct = parts[3].isEmpty() ? new byte[0] : Base64.getDecoder().decode(parts[3]);

            SecretKey passKey = deriveKey(passphrase, salt, 256);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, passKey, new IvParameterSpec(iv));
            String plaintext = new String(cipher.doFinal(ct), StandardCharsets.UTF_8);

            // Optionally clear existing vault
            Path vault = Paths.get(VAULT_FILE);
            if (overwrite) {
                Files.write(vault, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                if (!Files.exists(vault)) {
                    Files.createFile(vault);
                }
            }

            int count = 0;
            if (!plaintext.isBlank()) {
                for (String entry : plaintext.split("\\R")) {
                    if (entry.isBlank()) continue;
                    int idx = entry.indexOf(':');
                    if (idx <= 0) continue;
                    String acc = entry.substring(0, idx);
                    String pwd = entry.substring(idx + 1);
                    addPassword(acc, pwd); // re-encrypts with local AES key
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return -1;
        }
    }

    // PBKDF2 key derivation for export/import
    private static SecretKey deriveKey(String passphrase, byte[] salt, int bits) throws Exception {
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, 120_000, bits);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = f.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
