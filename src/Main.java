import java.util.Scanner;
import java.io.Console;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Password Vault ---");
            System.out.println("1. Add Password");
            System.out.println("2. View Passwords");
            System.out.println("3. Delete Password");
            System.out.println("4. Search Password");
            System.out.println("5. Update Password");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter account name: ");
                        String account = sc.nextLine();

                        // Secure password input
                        Console console = System.console();
                        String password;
                        if (console != null) {
                            char[] pwdArray = console.readPassword("Enter password: ");
                            password = new String(pwdArray);
                        } else {
                            System.out.print("Enter password: ");
                            password = sc.nextLine();
                        }

                        Vault.addPassword(account, password);
                        break;

                    case 2:
                        Vault.viewPasswords();
                        break;

                    case 3:
                        System.out.print("Enter account name to delete: ");
                        String delAccount = sc.nextLine();
                        Vault.deletePassword(delAccount);
                        break;

                    case 4:
                        System.out.print("Enter account name to search: ");
                        String searchAccount = sc.nextLine();
                        Vault.searchPassword(searchAccount);
                        break;

                    case 5:
                        System.out.print("Enter account name to update: ");
                        String updAccount = sc.nextLine();

                        Console console2 = System.console();
                        String newPassword;
                        if (console2 != null) {
                            char[] pwdArray2 = console2.readPassword("Enter new password: ");
                            newPassword = new String(pwdArray2);
                        } else {
                            System.out.print("Enter new password: ");
                            newPassword = sc.nextLine();
                        }

                        Vault.updatePassword(updAccount, newPassword);
                        break;

                    case 6:
                        System.out.println("Exiting... Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option! Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
