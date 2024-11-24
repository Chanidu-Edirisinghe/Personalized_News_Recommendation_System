import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the News Recommendation System!");
        System.out.println("Please choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");

        int choice = 0;
        int count = 0;
        while (choice != 1 && choice != 2) {
            System.out.print("Enter your choice (1 or 2): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice != 1 && choice != 2) {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }

        scanner.nextLine(); // Consume leftover newline character

        if (choice == 1) {
            System.out.println("Login selected.");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            SystemUser user = new SystemUser(); // Assuming default constructor
            boolean loginSuccess = user.login(username, password);

            if (loginSuccess) {
                System.out.println("Login successful! Welcome, " + username + "!");
                // Proceed to the main application logic
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } else if (choice == 2) {
            System.out.println("Register selected.");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter first name: ");
            String firstname = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastname = scanner.nextLine();

            User newUser = new User(count, username, password, firstname, lastname);
            boolean registrationSuccess = newUser.register();

            if (registrationSuccess) {
                System.out.println("Registration successful! Please log in to continue.");
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        }

        scanner.close();
    }
}
