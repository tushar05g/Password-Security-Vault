import java.io.*;

public class Vault {
    private static final String VAULT_FILE = "vault.txt";

    // Add a new password
    public static void addPassword(String account, String password) {
        try (FileWriter fw = new FileWriter(VAULT_FILE, true)) {
            fw.write(account + ":" + password + "\n");
            System.out.println("✅ Password saved to vault.txt!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // View all stored passwords
    public static void viewPasswords() {
        File f = new File(VAULT_FILE);
        if (!f.exists()) {
            System.out.println("⚠ No passwords stored yet.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            System.out.println("\nStored Passwords:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("⚠ Error reading vault.");
        }
    }

    // Delete by account
    public static void deletePassword(String account) {
        File inputFile = new File(VAULT_FILE);
        if (!inputFile.exists()) {
            System.out.println("⚠ Vault empty.");
            return;
        }

        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(account + ":")) {
                    found = true;
                    continue;
                }
                writer.write(line + System.lineSeparator());
            }

            if (!found) {
                System.out.println("⚠ Account not found!");
            } else {
                System.out.println("🗑 Deleted password for account: " + account);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // Search account
    public static void searchPassword(String account) {
        File f = new File(VAULT_FILE);
        if (!f.exists()) {
            System.out.println("⚠ Vault empty.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(account + ":")) {
                    System.out.println("🔎 Found: " + line);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("⚠ No password found for: " + account);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
