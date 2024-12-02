import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Driver {
    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // Allow up to 10 concurrent users
    public static void main(String[] args) {
        while (true) {
            boolean condition = handleMainMenu(); // Display the main menu
            if(condition){
                break;
            }
        }
        executorService.shutdown();
    }

    // Fixed methods

    private static boolean notAlphabeticName(String name) {
        // Check if the name matches only alphabetic letters
        return !name.matches("[a-zA-Z]+");
    }
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
        while(true) {
            System.out.println("1. Do you like this article.");
            System.out.println("2. Do you want to skip this article.");
            System.out.println("3. Exit article.");
            System.out.print("Enter your choice (1, 2, 3): ");
            String choice = scanner.nextLine().trim(); // validation
            switch (choice) {
                case "1":
                    user.recordInteraction(user, article, "Like");
                    break;
                case "2":
                    user.recordInteraction(user, article, "Skip");
                    break;
                case "3":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static List<String> handleLogin() {
        System.out.println("Login selected.");
        String username = validateUsername(false);
        String password = validatePassword(false);

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
                    Article currentArticle = handleDisplayArticles(user, dbHandler.fetchArticles());
                    handleUserInteraction(user, currentArticle);
                    break;
                case "3": // Get recommendations
                    List<Article> recommendedArticles = user.getRecommendations();
                    Article currentRecArticle = handleDisplayArticles(user, recommendedArticles);
                    handleUserInteraction(user, currentRecArticle);
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
    private static String validateUsername(boolean update) {
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();

            if(update && username.isEmpty()){
                return null;
            }

            if (username.length() < 6) {
                System.out.println("Username must be at least 6 characters long. Please try again.");
                continue;
            }

            if (!username.matches("[a-zA-Z0-9]+")) {
                System.out.println("Username must only contain alphanumeric characters. Please try again.");
                continue;
            }

            break; // If all checks pass, exit the loop
        }
        return username;
    }
    private static String validatePassword(boolean update){
        String password;
        while (true) {
            System.out.print("Enter password: ");
            password = scanner.nextLine().trim();

            if(update && password.isEmpty()){
                return null;
            }

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

            // Check if name is empty
            if (name.isEmpty()) {
                System.out.println(fieldName + " cannot be empty. Please try again.");
                continue;
            }

            // Check if the name is alphabetic
            if (notAlphabeticName(name)) {
                System.out.println(fieldName + " must only contain alphabetic characters. Please try again.");
                continue;
            }

            break; // If all checks pass, exit the loop
        }
        return name;
    }
    private static void handlePwReset(Admin admin){
        System.out.println("Password reset selected.\n");
        int user_id = validateID("user ID");
        admin.resetPassword(user_id);
    }
    private static void handleUserDeactivation(Admin admin){
        System.out.println("Deactivate user selected.");
        int user_id = validateID("user ID");
        System.out.println("Enter user ID to reconfirm deletion.");
        int re_user_id = validateID("user ID");
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
                // Submit the login task to the executor for concurrent execution
                executorService.submit(() -> {
                    List<String> userDetails = handleLogin(); // Call the login flow
                    // go to main flow only if the user is registered in the system.
                    if (userDetails != null) {
                        // check if a user logged in
                        if (userDetails.getLast().equals("USER")) {
                            User user = createUser(userDetails);
                            handleUserMenu(user);
                        } else { // admin
                            Admin admin = createAdmin(userDetails);
                            handleAdminMenu(admin);
                        }
                    }
                });
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
    private static void handleAddArticle(Admin admin){
        System.out.println("Add selected\n");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter content: ");
        String content = scanner.nextLine();
        System.out.println("Please wait a moment for the article to get categorized.");
        String category = Objects.requireNonNull(ArticleCategorizer.categorizeArticles(title + " " + content)).toUpperCase();
        Article article = admin.addArticle(title, content, Category.valueOf(category));
        if(article != null){
            System.out.println("Article added.");
        }
    }
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
                    handleAddArticle(admin);
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
    // fix if needed
    private static void handleProfileUpdate(User currentUser) {
        System.out.println("\nProfile Update selected.\n");

        // Collect new details from the user
        System.out.println("Enter new username (leave blank to keep current).");
        String username;
        while (true) {
            username = validateUsername(true);
            if(username == null){
                username = currentUser.getUsername();
                break;
            }
            if (!dbHandler.checkUsernameAvailability(username) && !Objects.equals(currentUser.getUsername(), username)) {
                System.out.println("Username is already taken. Please choose another.");
                continue;
            }
            break;
        }


        System.out.println("Enter new password (leave blank to keep current): ");
        String password;
        password = validatePassword(true);
        if (password == null) {
            password = currentUser.getPassword(); // Keep the current password
        }


        System.out.println("Enter new first name (leave blank to keep current).");
        String firstName;
        while(true) {
            firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                firstName = currentUser.getFirstName();
                break;
            }
            else if(notAlphabeticName(firstName)) {
                System.out.print("Enter valid first name: ");
            }
        }

        System.out.println("Enter new last name (leave blank to keep current).");
        String lastName;
        while(true) {
            lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                lastName = currentUser.getLastName();
                break;
            }
            else if(notAlphabeticName(lastName)) {
                System.out.print("Enter valid last name: ");
            }
        }

        // Update user details
        currentUser.updateDetails(username, password, firstName, lastName);

        // update Preferences
        handlePreferenceUpdate(currentUser, true);

        System.out.println("\nProfile updated successfully!\n");
    }
    private static Article handleDisplayArticles(User user, List<Article> articles) {
        user.viewArticles(articles);

        int article_id;
        Set<Integer> articleIds = articles.stream()
                .map(Article::getArticleID) // Assuming Article has a getId() method
                .collect(Collectors.toSet()); // Collect all article IDs into a set for quick lookup

        while (true) {
            article_id = validateID("article ID"); // Validate user input
            if (!articleIds.contains(article_id)) { // Check if the ID exists in the articles list
                System.out.println("This article ID is not valid. Please try again.");
            } else {
                break; // Exit loop if a valid ID is entered
            }
        }
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
    }
    private static void handleDeleteArticle(Admin admin){
        admin.viewArticles(dbHandler.fetchArticles());
        int article_id = validateID("article ID");
        admin.deleteArticle(article_id);
    }
    private static void handleEditArticle(Admin admin) {
        List<Article> articles = dbHandler.fetchArticles();
        admin.viewArticles(articles);
        int article_id = validateID("article ID");
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
    private static void handlePreferenceUpdate(User user, boolean preferenceAdded){
        System.out.println("Choose categories you are interested in.");
        Category[] categories = Category.values();

        // Display available categories
        for (int i = 0; i < categories.length - 1; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }

        System.out.println("\nEnter category numbers one by one. Enter 'x' to stop.");
        System.out.println("Example: Enter 1 to select CULTURE.");

        // Initialize a set to avoid duplicate inputs
        Set<Integer> selectedCategories = new HashSet<>();

        while (true) {
            System.out.print("\nEnter number: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("x")) {
                break;
            }

            try {
                int categoryNumber = Integer.parseInt(input);

                if (categoryNumber < 1 || categoryNumber > categories.length - 1) {
                    System.out.println("Invalid number. Please enter a number between 1 and " + (categories.length - 1) + ".");
                } else if (selectedCategories.contains(categoryNumber)) {
                    System.out.println("You have already selected this category. Choose a different one.");
                } else {
                    selectedCategories.add(categoryNumber);
                    System.out.println(categories[categoryNumber - 1] + " added to your preferences.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or 'x' to stop.");
            }
        }

        // Convert selected category numbers to preferences
        if(!preferenceAdded) {
            for (int i = 0; i < categories.length - 1; i++) {
                user.addPreference(new Preference(categories[i], 0), true); // Initialize all categories with a default interest level of 0
            }
        }
        else{
            for (int i = 0; i < categories.length - 1; i++) {
                user.addPreference(new Preference(categories[i], 0), false); // Initialize all categories with a default interest level of 0
                user.updatePreferences(i, 0);
            }
        }

        for (int categoryNumber : selectedCategories) {
            user.updatePreferences(categoryNumber - 1, 50); // Update selected categories with interest level
        }
    }
    private static User handleRegister() {
        System.out.println("Registration selected.");

        // Validate username input
        String username;
        while (true) {
            username = validateUsername(false);
            if (!dbHandler.checkUsernameAvailability(username)) {
                System.out.println("Username is already taken. Please choose another.");
                continue;
            }
            break;
        }
        // Validate password input
        String password = validatePassword(false);
        // Validate first name input
        String firstName = validateName("first name");
        // Validate last name input
        String lastName = validateName("last name");

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

            handlePreferenceUpdate(user, false);
            System.out.println("\nPreferences updated successfully!");
            System.out.println("Registration successful! Welcome, " + user.getFirstName() + "!");
            return user;
        } else {
            System.out.println("Registration failed. Please try again.");
            return null;
        }
    }
}
