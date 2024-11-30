import java.time.LocalDate;
import java.util.*;

public class Driver {
    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        while (true) {
            boolean condition = handleMainMenu(); // Display the main menu
            if(condition){
                break;
            }
        }
    }

    // Fixed methods

    private static int validateID(String idName) {
        int id;
        while (true) {
            System.out.print("Enter " + idName + ": ");
            String input = scanner.nextLine().trim();

            try {
                id = Integer.parseInt(input); // Try parsing the input to an integer
                if (id > 0) {
                    break; // Valid ID (numeric and greater than zero)
                } else {
                    System.out.println(idName + " must be greater than zero. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println(idName + " must be a valid number. Please try again.");
            }
        }
        return id;
    }
    private static void handleUserInteraction(User user, Article article) {
        // 'Read', 'Like', 'Skip'
        user.recordInteraction(user, article, "Read");
        System.out.println("1. Do you like this article.");
        System.out.println("2. Do you want to skip this article.");
        System.out.print("Enter your choice (1, 2): ");
        String choice = scanner.nextLine().trim(); // validation
        switch (choice){
            case "1":
                user.recordInteraction(user, article, "Like");
            case "2":
                user.recordInteraction(user, article, "Skip");
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    private static List<String> handleLogin() {
        System.out.println("Login selected.");
        String username = validateUsername();
        String password = validatePassword();

        // check if login details are in the DB
        if (dbHandler.login(username, password)) {
            // return the user details
            return dbHandler.getUserDetails(username);
        } else {
            System.out.println("Invalid username or password. Try again.");
            return null;
        }
    }
    private static void handleUserMenu(User user) {
        System.out.println("Login successful! Welcome, " + user.getUsername());
        // Proceed with user-specific actions

        while (true) {
            System.out.println("Please choose an option:");
            System.out.println("1. Manage Profile");
            System.out.println("2. View articles");
            System.out.println("3. Get recommendations");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1, 2, 3, 4): ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": // Manage Profile
                    user.displayUserAccountDetails();
                    handleProfileUpdate(user);
                    break;
                case "2": // View articles
                    Article currentArticle = handleDisplayArticles(user);
                    handleUserInteraction(user, currentArticle);
                    break;
                case "3": // Get recommendations
                    user.getRecommendations(); // use method to print the articles gracefully
                    break;
                case "4": // Exit to main menu/Logout
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            if (choice.equals("4")) {
                break;
            }
        }
    } // adjust if needed
    private static String validateUsername(){
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();
            if (username.length() < 6) {
                System.out.println("Username must be at least 6 characters long. Please try again.");
                continue;
            }
            if (!dbHandler.checkUsernameAvailability(username)) {
                System.out.println("Username is already taken. Please choose another.");
                continue;
            }
            break;
        }
        return username;
    }
    private static String validatePassword(){
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
        return password;
    }
    private static String validateName(String fieldName) {
        String name;
        while (true) {
            System.out.print("Enter " + fieldName + ": ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println(fieldName + " cannot be empty. Please try again.");
            } else {
                break;
            }
        }
        return name;
    }
    private static void handlePwReset(Admin admin){
        System.out.println("Password reset selected.\n");
        System.out.print("Please choose a user ID: ");
        String userID = scanner.nextLine().trim();
        int user_id = validateID(userID);
        admin.resetPassword(user_id);
    }
    private static void handleUserDeactivation(Admin admin){
        System.out.println("Deactivate user selected.");
        System.out.print("Please choose a user ID: ");
        String userID = scanner.nextLine().trim();
        int user_id = validateID(userID);
        System.out.print("Enter user ID to reconfirm deletion: ");
        String re_userID = scanner.nextLine().trim();
        int re_user_id = validateID(re_userID);
        if(user_id == re_user_id){
            admin.deactivateUserProfile(user_id);
        }
        else{
            System.out.println("Reconfirmation failed.");
        }
    }
    private static void handleManageUsers(Admin admin){
        while (true) {
            System.out.println("Please choose an option:");
            System.out.println("1. Reset user password.");
            System.out.println("2. Deactivate user account.");
            System.out.println("3. Go back.");
            System.out.print("Enter your choice (1, 2, 3): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    admin.displayRegisteredUsers();
                    handlePwReset(admin);
                    break;
                case "2":
                    admin.displayRegisteredUsers();
                    handleUserDeactivation(admin);
                    break;
                case "3":
                    System.out.println("Exiting menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static boolean handleMainMenu() {
        System.out.println("Welcome! Please choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice (1, 2, 3): ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                List<String> userDetails = handleLogin(); // Call the login flow
                // go to main flow only if the user is registered in the system.
                if(userDetails != null) {
                    // check if a user logged in
                    if(userDetails.getLast().equals("USER")) {
                        User user = createUser(userDetails);
                        handleUserMenu(user);
                    }
                    else{ // admin
                        Admin admin = createAdmin(userDetails);
                        handleAdminMenu(admin);
                    }
                }
                break;

            case "2":
                User newUser = handleRegister();
                if (newUser != null) { // Call the registration flow
                    System.out.println("You can now log in.");
                }
                break;

            case "3":
                System.out.println("Exiting application. Goodbye!");
                scanner.close();
                return true;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return false;
    }
    private static User createUser(List<String> userDetails){
        return new User(Integer.parseInt(userDetails.get(0)), userDetails.get(1), userDetails.get(2), userDetails.get(3),
                userDetails.get(4), LocalDate.parse(userDetails.get(5)));
    }
    private static Admin createAdmin(List<String> userDetails){
        return new Admin(Integer.parseInt(userDetails.get(0)), userDetails.get(1), userDetails.get(2), userDetails.get(3),
                userDetails.get(4), LocalDate.parse(userDetails.get(5)));
    }
    private static void handleAddArticle(){
        while (true) {
            System.out.println("Please choose an option:");
            System.out.println("1. Add manually");
            System.out.println("2. Add from external DB");
            System.out.println("3. Go back");
            System.out.print("Enter your choice (1, 2, 3): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleManualAdd();
                    break;
                case "2":
                    System.out.println("External DB selected");
                    break;
                case "3":
                    System.out.println("Exiting menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }  // check external DB part
    private static void handleManageArticles(Admin admin){
        while(true) {
            System.out.println("Please choose an option:");
            System.out.println("1. Add article.");
            System.out.println("2. Edit article.");
            System.out.println("3. Delete article.");
            System.out.println("4. Go back.");
            System.out.print("Enter your choice (1, 2, 3, 4): ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    // add articles
                    handleAddArticle();
                    break;
                case "2":
                    // edit article
                    handleEditArticle(admin);
                    break;
                case "3":
                    // delete article
                    handleDeleteArticle(admin);
                    break;
                case "4":
                    System.out.println("Exiting menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void handleAdminMenu(Admin admin) {
        while (true) {
            System.out.println("Welcome! Please choose an option:");
            System.out.println("Please choose an option:");
            System.out.println("1. Manage Users"); // reset password, deactivate accounts
            System.out.println("2. Manage articles"); // add, edit, delete articles
            System.out.println("3. Logout");
            System.out.print("Enter your choice (1, 2, 3): ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": // Manage users
                    handleManageUsers(admin);
                    break;
                case "2": // Manage articles
                    handleManageArticles(admin);
                    break;
                case "3":
                    System.out.println("Exiting menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void handleManualAdd() {
        System.out.println("Manual selected\n");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter content: ");
        String content = scanner.nextLine();
        String category = Objects.requireNonNull(ArticleCategorizer.categorizeArticles(title + " " + content)).toUpperCase();
        Article article = dbHandler.saveNewArticle(title, content, Category.valueOf(category));
        if(article != null){
            System.out.println("Article added.");
        }
    } // fix if needed
    private static void handleProfileUpdate(User currentUser) {
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
    private static Article handleDisplayArticles(User user) {

        List<Article> articles = user.viewArticles();

        System.out.println("Please choose an option:");
        System.out.println("1. Select article to read");
        System.out.println("2. Filter articles by category");  // optional
        System.out.print("Enter your choice (1, 2): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                int article_id = validateID("article ID");

                // Search for the article with the matching ID
                Article matchedArticle = null;
                for (Article article : articles) {
                    if (article.getArticleID() == article_id) {
                        matchedArticle = article;
                        break;
                    }
                }

                // If an article is found, display it; otherwise, print an error message
                if (matchedArticle != null) {
                    matchedArticle.displayArticle();
                    return matchedArticle;
                } else {
                    System.out.println("No article found with the given ID.");
                    return  null;
                }
            case "2":
                // filter the articles and display to user
                // ask for article id to view
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return null;
    }
    private static void handleDeleteArticle(Admin admin){
        admin.viewArticles();
        System.out.print("\nEnter article ID: ");
        String articleID = scanner.nextLine().trim();
        int article_id = validateID(articleID);
        admin.deleteArticle(article_id);
    }
    private static void handleEditArticle(Admin admin) {
        List<Article> articles = admin.viewArticles();
        System.out.print("\nEnter article ID: ");
        String articleID = scanner.nextLine();

        int article_id = validateID(articleID);
        Article selectedArticle = null;
        for (Article article : articles) {
            if (article.getArticleID() == article_id) {
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
        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();

        System.out.print("Enter new content: ");
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



    // To be fixed methods( fix category input issue)


    private static User handleRegister() {
        System.out.println("Registration selected.");

        // Validate username input
        String username = validateUsername();
        // Validate password input
        String password = validatePassword();
        // Validate first name input
        String firstName = validateName("First name");
        // Validate last name input
        String lastName = validateName("Last name");

        // Attempt to register the user
        int userId = dbHandler.register(username, password, firstName, lastName);
        if (userId > 0) {
            // Retrieve user details
            List<String> userDetails = dbHandler.getUserDetails(username);

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
            return null;
        }
    }

}
