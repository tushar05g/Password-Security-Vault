import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Master password gate
        if (!java.nio.file.Files.exists(java.nio.file.Paths.get("master.key"))) {
            System.out.print("Set a master password: ");
            String newPass = sc.nextLine();
            Auth.setupMaster(newPass);
        } else {
            System.out.print("Enter master password: ");
            String input = sc.nextLine();
            if (!Auth.verifyMaster(input)) {
                System.out.println("❌ Wrong master password. Exiting...");
                return;
            }
            System.out.println("✅ Access granted!");
        }

        while (true) {
            System.out.println("\n=== Password Vault ===");
            System.out.println("1. Add Password");
            System.out.println("2. View Passwords");
            System.out.println("3. Delete Password");
            System.out.println("4. Search Password");
            System.out.println("5. Change Master Password");
            System.out.println("6. Generate Strong Password");
            System.out.println("7. Export Vault (with passphrase)");
            System.out.println("8. Import Vault (with passphrase)");
            System.out.println("9. Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("⚠ Invalid input. Enter a number!");
                continue;
            }

            switch (choice) {
                case 1: {
                    System.out.print("Enter account: ");
                    String account = sc.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine().trim();
                    Vault.addPassword(account, password);
                    break;
                }
                case 2:
                    Vault.viewPasswords();
                    break;

                case 3: {
                    System.out.print("Enter account to delete: ");
                    String delAcc = sc.nextLine().trim();
                    Vault.deletePassword(delAcc);
                    break;
                }

                case 4: {
                    System.out.print("Enter account to search: ");
                    String searchAcc = sc.nextLine().trim();
                    Vault.searchPassword(searchAcc);
                    break;
                }

                case 5: {
                    System.out.print("Enter current master password: ");
                    String oldPass = sc.nextLine();
                    if (Auth.verifyMaster(oldPass)) {
                        System.out.print("Enter new master password: ");
                        String newPass = sc.nextLine();
                        Auth.changeMaster(newPass);
                    } else {
                        System.out.println("❌ Wrong master password. Cannot change.");
                    }
                    break;
                }

                case 6: {
                    System.out.print("Enter password length (min 6): ");
                    int len;
                    try {
                        len = Integer.parseInt(sc.nextLine());
                    } catch (Exception e) {
                        System.out.println("⚠ Invalid length.");
                        break;
                    }
                    System.out.print("Include numbers? (y/n): ");
                    boolean useNums = sc.nextLine().trim().equalsIgnoreCase("y");
                    System.out.print("Include special characters? (y/n): ");
                    boolean useSpecial = sc.nextLine().trim().equalsIgnoreCase("y");

                    String genPass = Vault.generatePassword(len, useNums, useSpecial);
                    System.out.println("✅ Strong password generated: " + genPass);
                    System.out.print("Do you want to save it? (y/n): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                        System.out.print("Enter account name: ");
                        String acc = sc.nextLine().trim();
                        Vault.addPassword(acc, genPass);
                    }
                    break;
                }

                case 7: { // Export
                    System.out.print("Enter export file path (e.g., vault_export.psv): ");
                    String path = sc.nextLine().trim();
                    System.out.print("Set an export passphrase: ");
                    String pass = sc.nextLine();
                    int count = Vault.exportVault(path, pass);
                    if (count >= 0) {
                        System.out.println("📤 Exported " + count + " entries to: " + path);
                    } else {
                        System.out.println("❌ Export failed.");
                    }
                    break;
                }

                case 8: { // Import
                    System.out.print("Enter import file path: ");
                    String path = sc.nextLine().trim();
                    System.out.print("Enter import passphrase: ");
                    String pass = sc.nextLine();
                    System.out.print("Overwrite existing vault? (y/n): ");
                    boolean overwrite = sc.nextLine().trim().equalsIgnoreCase("y");
                    int count = Vault.importVault(path, pass, overwrite);
                    if (count >= 0) {
                        System.out.println("📥 Imported " + count + " entries into your vault.");
                    } else {
                        System.out.println("❌ Import failed.");
                    }
                    break;
                }

                case 9:
                    System.out.println("👋 Exiting...");
                    return;

                default:
                    System.out.println("⚠ Invalid option!");
            }
        }
    }
}
