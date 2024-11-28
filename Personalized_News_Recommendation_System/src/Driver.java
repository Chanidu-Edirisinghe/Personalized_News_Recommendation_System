import java.time.LocalDate;
import java.util.*;

public class Driver {
    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu(); // Display the main menu
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    List<String> userDetails = handleLogin(scanner); // Call the login flow
                    if(userDetails != null) {
                        if(userDetails.getLast().equals("USER")) {
                            User user = createUser(userDetails);
                            System.out.println("Login successful! Welcome, " + user.getUsername());
                            // Proceed with user-specific actions
                            while (true) {
                                displayUserMenu();
                                String choice1 = scanner.nextLine();
                                switch (choice1) {
                                    case "1":
                                        user.displayUserAccountDetails();
                                        handleProfileUpdate(scanner, user);
                                        break;
                                    case "2":
                                        user.viewArticles();
                                        break;
                                    case "3":
                                    case "4":
                                        System.out.println("Exiting user menu...");
                                        break;
                                    default:
                                        System.out.println("Invalid choice. Please try again.");
                                }
                                if (choice1.equals("4")) {
                                    break;
                                }
                            }
                        }
                        else{
                            // admin menu
                            displayAdminMenu();
                            Admin admin = createAdmin(userDetails);
                            String choice2 = scanner.nextLine();
                            switch (choice2) {
                                case "1":

                                    break;
                                case "2":

                                    break;
                            }
                        }
                    }
                    break;

                case "2":
                    User newUser = handleRegister(scanner);
                    if (newUser != null) { // Call the registration flow
                        System.out.println("You can now log in.");
                    }
                    break;

                case "3":
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Displays the main menu
    private static void displayMenu() {
        System.out.println("Welcome! Please choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1, 2, 3): ");
    }

    private static void displayUserMenu() {
        System.out.println("Welcome! Please choose an option:");
        System.out.println("Please choose an option:");
        System.out.println("1. Manage Profile");
        System.out.println("2. View articles");
        System.out.println("3. Get recommendations");
        System.out.println("4. Logout");
        System.out.print("Enter your choice (1, 2, 3, 4): ");
    }

    private static void displayAdminMenu() {
        System.out.println("Welcome! Please choose an option:");
        System.out.println("Please choose an option:");
        System.out.println("1. Manage Users"); // reset password, deactivate accounts
        System.out.println("2. Manage articles"); // add, edit, delete articles
        System.out.print("Enter your choice (1, 2): ");
    }

    // Handles the login flow
    private static List<String> handleLogin(Scanner scanner) {
        //dbHandler.connect();
        System.out.println("Login selected.");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (dbHandler.login(username, password)) {
            return dbHandler.getUserDetails(username);
            //dbHandler.closeConnection();
        } else {
            System.out.println("Invalid username or password. Try again.");
            return null;
        }
    }

    // Handles the registration flow
    private static User handleRegister(Scanner scanner) {
        //dbHandler.connect();
        System.out.println("Registration selected.");

        // Validate username input
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();
            if (username.length() < 3) {
                System.out.println("Username must be at least 3 characters long. Please try again.");
                continue;
            }
            if (!dbHandler.checkUsernameAvailability(username)) {
                System.out.println("Username is already taken. Please choose another.");
                continue;
            }
            break;
        }

        // Validate password input
        String password;
        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters long. Please try again.");
            } else {
                break;
            }
        }

        // Validate first name input
        String firstName;
        while (true) {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                System.out.println("First name cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Validate last name input
        String lastName;
        while (true) {
            System.out.print("Enter last name: ");
            lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                System.out.println("Last name cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Attempt to register the user
        int userId = dbHandler.register(username, password, firstName, lastName);
        if (userId > 0) {
            // Retrieve user details
            List<String> userDetails = dbHandler.getUserDetails(username);
            //dbHandler.closeConnection();

            // Create and return User object
            User user = new User(
                    Integer.parseInt(userDetails.get(0)),
                    userDetails.get(1),
                    userDetails.get(2),
                    userDetails.get(3),
                    userDetails.get(4),
                    LocalDate.parse(userDetails.get(5))
            );
            System.out.println("Registration successful! Welcome, " + user.getFirstName() + "!");
            return user;
        } else {
            System.out.println("Registration failed. Please try again.");
            //dbHandler.closeConnection();
            return null;
        }
    }

    private static void handleProfileUpdate(Scanner scanner, User currentUser) {
        System.out.println("\nProfile Update selected.\n");

        // Collect new details from the user
        System.out.print("Enter new username (leave blank to keep current): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = currentUser.getUsername();
        } else if (!dbHandler.checkUsernameAvailability(username)) {
            System.out.println("Username is already taken. Keeping current username.");
            username = currentUser.getUsername();
        }

        System.out.print("Enter new password (leave blank to keep current): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            password = currentUser.getPassword(); // Keep the current password
        }

        System.out.print("Enter new first name (leave blank to keep current): ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isEmpty()) {
            firstName = currentUser.getFirstName();
        }

        System.out.print("Enter new last name (leave blank to keep current): ");
        String lastName = scanner.nextLine().trim();
        if (lastName.isEmpty()) {
            lastName = currentUser.getLastName();
        }

        // Update user details
        currentUser.updateDetails(username, password, firstName, lastName);

        System.out.println("\nProfile updated successfully!\n");
    }

    private static User createUser(List<String> userDetails){
        return new User(Integer.parseInt(userDetails.get(0)), userDetails.get(1), userDetails.get(2), userDetails.get(3),
                userDetails.get(4), LocalDate.parse(userDetails.get(5)));
    }

    private static Admin createAdmin(List<String> userDetails){
        return new Admin(Integer.parseInt(userDetails.get(0)), userDetails.get(1), userDetails.get(2), userDetails.get(3),
                userDetails.get(4), LocalDate.parse(userDetails.get(5)));
    }










//
//    public static void m1(String[] args) {
//        String back = "";
//        DatabaseHandler dbh = new DatabaseHandler();
//        List<Integer> preferenceNums = null;
//        int c = 10;
//        while(c > 0) {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Welcome to the News Recommendation System!");
//            System.out.println("Please choose an option:");
//            System.out.println("1. Login");
//            System.out.println("2. Register");
//
//            int choice = 0;
//            while (choice != 1 && choice != 2) {
//                System.out.print("Enter your choice (1 or 2): ");
//                if (scanner.hasNextInt()) {
//                    choice = scanner.nextInt();
//                    if (choice != 1 && choice != 2) {
//                        System.out.println("Invalid choice. Please enter 1 or 2.");
//                    }
//                } else {
//                    System.out.println("Invalid input. Please enter a number.");
//                    scanner.next(); // Clear the invalid input
//                }
//            }
//
//            scanner.nextLine(); // Consume leftover newline character
//
//            if (choice == 1) {
//                while(true) {
//                    System.out.println("Login selected.");
//                    System.out.println("Enter x to go back to menu.");
//                    back = scanner.nextLine();
//                    if (back.equalsIgnoreCase("x")) {
//                        break;
//                    }
//                    System.out.print("Enter username: ");
//                    String username = scanner.nextLine();
//                    System.out.print("Enter password: ");
//                    String password = scanner.nextLine();
//
//                    List<String> userDetails = SystemUser.login(username, password);
//
//                    if (userDetails != null && Objects.equals(userDetails.getLast(), "USER")) {
//                        User user = new User(
//                                Integer.parseInt(userDetails.get(0)),
//                                userDetails.get(1),
//                                userDetails.get(2),
//                                userDetails.get(3),
//                                userDetails.get(4)
//                        );
//                        System.out.println("Login successful! Welcome, " + userDetails.get(4) + "!");
//                        System.out.println("Enter x to go back to menu.");
//                        back = scanner.nextLine();
//                        if (back.equalsIgnoreCase("x")) {
//                            break;
//                        }
//                        System.out.println("Please choose an option:");
//                        System.out.println("1. Manage Profile");
//                        System.out.println("2. View articles");
//                        System.out.println("3. Get recommendations");
//                        System.out.print("Enter your choice (1, 2, 3): ");
//                        choice = 0;
//                        while (choice != 1 && choice != 2 && choice != 3) {
//                            if (scanner.hasNextInt()) {
//                                choice = scanner.nextInt();
//                                if (choice != 1 && choice != 2 && choice != 3) {
//                                    System.out.println("Invalid choice. Please enter 1, 2, 3.");
//                                }
//                            } else {
//                                System.out.println("Invalid input. Please enter a number.");
//                                scanner.next(); // Clear the invalid input
//                            }
//                        }
//
//                        scanner.nextLine(); // Consume leftover newline character
//                        System.out.println("The rest of the process to be filled.");
//                        // Add options for user.
//                    } else if (userDetails != null && Objects.equals(userDetails.getLast(), "ADMIN")) {
//                        continue;
//                    } else {
//                        System.out.println("Invalid credentials. Please try again.");
//                    }
//                }
//            }
//            else {
//                while(true) {
//                    System.out.println("Register selected.");
//
//                    System.out.println("Enter x to go back to menu.");
//                    back = scanner.nextLine();
//                    if (back.equalsIgnoreCase("x")) {
//                        break;
//                    }
//
//                    System.out.print("Enter username: ");
//                    String username = scanner.nextLine();
//                    dbh.connect();
//                    while (!dbh.checkUsernameAvailability(username)) {
//                        System.out.print("Enter username: ");
//                        username = scanner.nextLine();
//                    }
//                    dbh.closeConnection();
//
//                    System.out.print("Enter password: ");
//                    String password = scanner.nextLine();
//                    System.out.print("Enter first name: ");
//                    String firstname = scanner.nextLine();
//                    System.out.print("Enter last name: ");
//                    String lastname = scanner.nextLine();
//
//                    System.out.println("Please enter the news article categories you like.");
//                    System.out.println("Input format: 1,2,3,7,10,...");
//                    int v1 = 1;
//                    for (Category category : Category.values()) {
//                        System.out.format("%d.  %s\n", v1, category);
//                        v1++;
//                    }
//                    boolean isValid = false;
//                    while(!isValid) {
//                        String preferences = scanner.nextLine();
//                        preferenceNums= Arrays.stream(preferences.split(","))
//                                .map(Integer::parseInt)
//                                .toList();
//
//                        isValid = preferenceNums.stream()
//                                .allMatch(num -> num >= 1 && num <= 10);
//
//                        if (!isValid) {
//                            System.out.println("Invalid");
//                        } else {
//                            System.out.println("Valid");
//                        }
//                    }
//
//                    User newUser;
//                    int user_id = User.register(username, password, firstname, lastname);
//                    if (user_id != -1) {
//                        newUser = new User(user_id, username, password, firstname, lastname);
//
//                        Category[] categories = Category.values();
//                        for(Category category: categories){
//                            newUser.addPreference(new Preference(category, 0));
//                        }
//
//                        for (int pref: preferenceNums){
//                            newUser.updatePreferences(pref-1, categories[pref-1], 1);
//                        }
//
//                        System.out.println("Registration successful! Login to continue.");
//                        break;
//                    } else {
//                        System.out.println("Registration failed. Please try again.");
//                    }
//                }
//            }
//            c--;
//        }
//        //scanner.close();
//    }






}
