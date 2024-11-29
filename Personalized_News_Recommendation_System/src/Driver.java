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
                                        List<Article> articles = user.viewArticles();
                                        handleViewArticles();
                                        String choice2 = scanner.nextLine();
                                        switch (choice2) {
                                            case "1":
                                                handleDisplayArticle(scanner, articles);
                                                break;
                                            case "2":
                                                // filter the articles and display to user
                                                // ask for article id to view
                                                break;
                                        }
                                        break;
                                    case "3":
                                        // get recommendations
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
                            while (true) {
                                displayAdminMenu();
                                Admin admin = createAdmin(userDetails);
                                String choice2 = scanner.nextLine();
                                switch (choice2) {
                                    case "1": // Manage users
                                        while (true) {
                                            handleManageUsers();
                                            String choice3 = scanner.nextLine();
                                            switch (choice3) {
                                                case "1":
                                                    admin.displayRegisteredUsers();
                                                    handlePwReset(admin, scanner);
                                                    break;
                                                case "2":
                                                    admin.displayRegisteredUsers();
                                                    handleUserDeactivation(admin, scanner);
                                                    break;
                                                case "3":
                                                    System.out.println("Exiting menu...");
                                                    break;
                                                default:
                                                    System.out.println("Invalid choice. Please try again.");
                                            }
                                            if (choice3.equals("3")) {
                                                break;
                                            }
                                        }
                                        break;
                                    case "2": // Manage articles
                                        while(true) {
                                            handleManageArticles();
                                            String choice4 = scanner.nextLine();
                                            switch (choice4) {
                                                case "1":
                                                    handleAddArticle();
                                                    while (true) {
                                                        String choice5 = scanner.nextLine();
                                                        switch (choice5) {
                                                            case "1":
                                                                handleManualAdd(scanner);
                                                                break;
                                                            case "2":
                                                                System.out.println("External DB selected");
                                                                break;
                                                            case "3":
                                                                System.out.println("Json files selected");
                                                                break;
                                                            case "4":
                                                                System.out.println("Exiting menu...");
                                                                break;
                                                            default:
                                                                System.out.println("Invalid choice. Please try again.");
                                                        }
                                                        if (choice5.equals("4")) {
                                                            break;
                                                        }
                                                    } // add articles
                                                    break;
                                                case "2":
                                                    // edit article
                                                    handleEditArticle(admin, scanner);
                                                    break;
                                                case "3":
                                                    // delete article
                                                    handleDeleteArticle(admin, scanner);
                                                    break;
                                                case "4":
                                                    System.out.println("Exiting menu...");
                                                    break;
                                                default:
                                                    System.out.println("Invalid choice. Please try again.");
                                            }
                                            if (choice4.equals("4")) {
                                                break;
                                            }
                                        }
                                        break;
                                    case "3":
                                        System.out.println("Exiting menu...");
                                        break;
                                    default:
                                        System.out.println("Invalid choice. Please try again.");
                                }
                                if (choice2.equals("3")) {
                                    break;
                                }
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

    private static void handleAddArticle(){
        System.out.println("Please choose an option:");
        System.out.println("1. Add manually");
        System.out.println("2. Add from external DB");
        System.out.println("3. Add from json files");
        System.out.println("4. Go back");
        System.out.print("Enter your choice (1, 2, 3, 4): ");
    }

    private static void handleViewArticles(){
        System.out.println("Welcome! Please choose an option:");
        System.out.println("1. Select article to read");
        System.out.println("2. Filter articles by category");
        System.out.print("Enter your choice (1, 2): ");
    }

    private static void handlePwReset(Admin admin, Scanner scanner){
        System.out.println("Password reset selected.\n");
        try {
            System.out.print("Please choose a user ID: ");
            int user_id = Integer.parseInt(scanner.nextLine().trim());
            if (user_id <= 0) {
                System.out.println("User ID must be a positive number. Try again.");
            }
            else {
                admin.resetPassword(user_id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void handleUserDeactivation(Admin admin, Scanner scanner){
        System.out.println("Deactivate user selected.");
        try {
            System.out.print("Please choose a user ID: ");
            int user_id = Integer.parseInt(scanner.nextLine().trim());
            if (user_id <= 0) {
                System.out.println("User ID must be a positive number. Try again.");
            }
            else {
                System.out.print("Enter user ID to reconfirm deletion: ");
                int re_user_id = Integer.parseInt(scanner.nextLine().trim());
                if(user_id == re_user_id){
                    admin.deactivateUserProfile(user_id);
                }
                else{
                    System.out.println("Reconfirmation failed.");
                }

            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void handleManageUsers(){
        System.out.println("Please choose an option:");
        System.out.println("1. Reset user password.");
        System.out.println("2. Deactivate user account.");
        System.out.println("3. Go back.");
        System.out.print("Enter your choice (1, 2, 3): ");
    }

    private static void handleManageArticles(){
        System.out.println("Please choose an option:");
        System.out.println("1. Add article.");
        System.out.println("2. Edit article.");
        System.out.println("3. Delete article.");
        System.out.println("4. Go back.");
        System.out.print("Enter your choice (1, 2, 3, 4): ");
    }

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
        System.out.println("3. Logout");
        System.out.print("Enter your choice (1, 2, 3): ");
    }

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

            // Get initial preferences from user
            System.out.println("Choose categories you are interested in.");
            for(int i = 0; i < Category.values().length - 1; i++){
                System.out.println((i+1) +". "+ Category.values()[i]);
            }
            System.out.println("Enter category numbers one by one. Enter x to stop.");
            System.out.println("Example: Enter 1 to select CULTURE.");
            List<String> catNums = new ArrayList<>();
            while(true){
                System.out.print("\nEnter number: "); // add validation
                String number = scanner.nextLine().trim();
                if(number.equalsIgnoreCase("x")){
                    break;
                }
                catNums.add(number);
            }
            // set initial preferences for user
            Category[] categories = Category.values();
            for (int i = 0; i < categories.length - 1; i++) {
                user.addPreference(new Preference(categories[i], 0));
            }


            // update preferences based on input
            for(String catNum:catNums){
                user.updatePreferences(Integer.parseInt(catNum)-1, 5);
            }

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

    private static void handleManualAdd(Scanner scanner) {
        System.out.println("Manual selected\n");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter content: ");
        String content = scanner.nextLine();
        String category = Objects.requireNonNull(ArticleCategorizer.categorizeArticles(title + " " + content)).toUpperCase();
        // get recycled article ids from db if available, then make article object
        Article article = dbHandler.saveNewArticle(title, content, Category.valueOf(category));
        if(article != null){
            System.out.println("Article added.");
        }
    }

    private static void handleDisplayArticle(Scanner scanner, List<Article> articles) {
        System.out.print("Enter the article ID: ");
        String id = scanner.nextLine(); // add validation

        // Search for the article with the matching ID
        Article matchedArticle = null;
        for (Article article : articles) {
            if (article.getArticleID() == Integer.parseInt(id)) { // Assuming Article has a getId() method
                matchedArticle = article;
                break;
            }
        }

        // If an article is found, display it; otherwise, print an error message
        if (matchedArticle != null) {
            matchedArticle.displayArticle(); // Assuming Article has a displayArticle() method
        } else {
            System.out.println("No article found with the given ID.");
        }
    }

    private static void handleDeleteArticle(Admin admin, Scanner scanner){
        admin.viewArticles();
        System.out.print("\nEnter article ID: ");
        String id = scanner.nextLine();
        // validate id
        admin.deleteArticle(Integer.parseInt(id));
    }

    private static void handleEditArticle(Admin admin, Scanner scanner) {
        List<Article> articles = admin.viewArticles();
        System.out.print("\nEnter article ID: ");
        String id = scanner.nextLine();

        // Validate ID
        Article selectedArticle = null;
        for (Article article : articles) {
            if (article.getArticleID() == Integer.parseInt(id)) {
                selectedArticle = article;
                break;
            }
        }

        if (selectedArticle == null) {
            System.out.println("Invalid article ID.");
            return;
        }

        // Display the article details
        selectedArticle.displayArticle();

        // Prompt for new title and content, showing current values as defaults
        System.out.print("Enter new title (current: \"" + selectedArticle.getTitle() + "\"): ");
        String newTitle = scanner.nextLine();

        System.out.print("Enter new content (current: \"" + selectedArticle.getContent() + "\"): ");
        String newContent = scanner.nextLine();

        // Update title and content only if input is provided
        if (!newTitle.trim().isEmpty()) {
            selectedArticle.setTitle(newTitle);
        }

        if (!newContent.trim().isEmpty()) {
            selectedArticle.setContent(newContent);
        }
        admin.editArticle(selectedArticle);
        System.out.println("Article updated successfully!");
    }


}
