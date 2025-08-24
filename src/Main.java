import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Check if master password exists
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

        // Show menu
        while (true) {
            System.out.println("\n=== Password Vault ===");
            System.out.println("1. Add Password");
            System.out.println("2. View Passwords");
            System.out.println("3. Delete Password");
            System.out.println("4. Search Password");
            System.out.println("5. Change Master Password");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("⚠ Invalid input. Enter a number!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter account: ");
                    String account = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();
                    Vault.addPassword(account, password);
                    break;

                case 2:
                    Vault.viewPasswords();
                    break;

                case 3:
                    System.out.print("Enter account to delete: ");
                    String delAcc = sc.nextLine();
                    Vault.deletePassword(delAcc);
                    break;

                case 4:
                    System.out.print("Enter account to search: ");
                    String searchAcc = sc.nextLine();
                    Vault.searchPassword(searchAcc);
                    break;

                case 5:
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

                case 6:
                    System.out.println("👋 Exiting...");
                    return;

                default:
                    System.out.println("⚠ Invalid option!");
            }
        }
    }
}
