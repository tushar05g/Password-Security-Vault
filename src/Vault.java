import java.io.*;
import java.util.*;
import java.util.Base64;

public class Vault {
    private static final String FILE_NAME = "vault.txt";

    public static void addPassword(String account, String password) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            String encrypted = Base64.getEncoder().encodeToString(password.getBytes());
            bw.write(account + ":" + encrypted);
            bw.newLine();
            System.out.println("Password saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving password: " + e.getMessage());
        }
    }

    public static void viewPasswords() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No passwords found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean hasData = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String account = parts[0];
                    String decrypted = new String(Base64.getDecoder().decode(parts[1]));
                    System.out.println(account + " -> " + decrypted);
                    hasData = true;
                }
            }
            if (!hasData) {
                System.out.println("No passwords stored.");
            }
        } catch (IOException e) {
            System.out.println("Error reading passwords: " + e.getMessage());
        }
    }

    public static void deletePassword(String accountToDelete) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No passwords to delete.");
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(accountToDelete + ":")) {
                    lines.add(line);
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
            return;
        }

        if (found) {
            System.out.println("Password for account '" + accountToDelete + "' deleted.");
        } else {
            System.out.println("Account not found.");
        }
    }
}
