import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String back = "";
        DatabaseHandler dbh = new DatabaseHandler();
        int c = 10;
        while(c > 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the News Recommendation System!");
            System.out.println("Please choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");

            int choice = 0;
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
                while(true) {
                    System.out.println("Login selected.");
                    System.out.println("Enter x to go back to menu.");
                    back = scanner.nextLine();
                    if (back.equalsIgnoreCase("x")) {
                        break;
                    }
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    List<String> userDetails = SystemUser.login(username, password);

                    if (userDetails != null && Objects.equals(userDetails.getLast(), "USER")) {
                        User user = new User(
                                Integer.parseInt(userDetails.get(0)),
                                userDetails.get(1),
                                userDetails.get(2),
                                userDetails.get(3),
                                userDetails.get(4)
                        );
                        System.out.println("Login successful! Welcome, " + userDetails.get(4) + "!");
                        System.out.println("Enter x to go back to menu.");
                        back = scanner.nextLine();
                        if (back.equalsIgnoreCase("x")) {
                            break;
                        }
                        System.out.println("Please choose an option:");
                        System.out.println("1. Manage Profile");
                        System.out.println("2. View articles");
                        System.out.println("3. Get recommendations");
                        System.out.print("Enter your choice (1, 2, 3): ");
                        choice = 0;
                        while (choice != 1 && choice != 2 && choice != 3) {
                            if (scanner.hasNextInt()) {
                                choice = scanner.nextInt();
                                if (choice != 1 && choice != 2 && choice != 3) {
                                    System.out.println("Invalid choice. Please enter 1, 2, 3.");
                                }
                            } else {
                                System.out.println("Invalid input. Please enter a number.");
                                scanner.next(); // Clear the invalid input
                            }
                        }

                        scanner.nextLine(); // Consume leftover newline character
                        System.out.println("The rest of the process to be filled.");
                        // Add options for user.
                    } else if (userDetails != null && Objects.equals(userDetails.getLast(), "ADMIN")) {
                        continue;
                    } else {
                        System.out.println("Invalid credentials. Please try again.");
                    }
                }
            }
            else {
                while(true) {
                    System.out.println("Register selected.");

                    System.out.println("Enter x to go back to menu.");
                    back = scanner.nextLine();
                    if (back.equalsIgnoreCase("x")) {
                        break;
                    }

                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    dbh.connect();
                    while (!dbh.checkUsernameAvailability(username)) {
                        System.out.print("Enter username: ");
                        username = scanner.nextLine();
                    }
                    dbh.closeConnection();

                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter first name: ");
                    String firstname = scanner.nextLine();
                    System.out.print("Enter last name: ");
                    String lastname = scanner.nextLine();

                    System.out.println("Please enter the news article categories you like.");
                    System.out.println("Input format: 1,2,3,7,10,...");
                    int v1 = 1;
                    for (Category category : Category.values()) {
                        System.out.format("%d.  %s\n", v1, category);
                        v1++;
                    }
                    String preferences = scanner.nextLine();
                    List<Integer> preferenceNums = Arrays.stream(preferences.split(",")).map(Integer::parseInt).toList();

                    // add preferences to db


                    User newUser = new User(username, password, firstname, lastname);
                    boolean registrationSuccess = newUser.register();

                    if (registrationSuccess) {
                        System.out.println("Registration successful! Please log in to continue.");
                        break;
                    } else {
                        System.out.println("Registration failed. Please try again.");
                    }
                }
            }
            c--;
        }
        //scanner.close();
    }
}
