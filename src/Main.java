import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Password Vault =====");
            System.out.println("1. Create password");
            System.out.println("2. View passwords");
            System.out.println("3. Delete password");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter account name: ");
                    String account = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();
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
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
