import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String url = "jdbc:sqlite:news_recommendation_system.db";

    public DatabaseHandler(){
    }

    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

    public boolean login(String username, String password){
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            // If a match is found, return true
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Issue while connecting to the database.");
        }
        return false; // Return false if no match is found or an error occurs
    }

    public int register(String username, String password, String firstname, String lastname) {
        String sql = "INSERT INTO Users (username, password, firstname, lastname, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, firstname);
            pst.setString(4, lastname);
            pst.setString(5, "USER");

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the generated user ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicate failure
    }

    public void savePreference(int userID, Preference preference){
        String sql = "INSERT INTO Preferences (user_id, category, interest_level) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, userID);
            pst.setString(2, String.valueOf(preference.getCategory()));
            pst.setInt(3, preference.getInterestLevel());
            pst.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Interaction saveInteraction(User user, Article article, String interaction_type) {
        String sql = "INSERT INTO Interactions (user_id, article_id, interaction_type) VALUES (?, ?, ?)";
        Interaction interaction = null;

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set values for the prepared statement
            pst.setInt(1, user.getUserID());
            pst.setInt(2, article.getArticleID());
            pst.setString(3, interaction_type);

            // Execute the update
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated keys
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int interactionId = rs.getInt(1); // Auto-generated interaction_id

                        // Fetch the interaction_date from the database
                        String dateQuery = "SELECT interaction_date FROM Interactions WHERE interaction_id = ?";
                        try (PreparedStatement dateStmt = conn.prepareStatement(dateQuery)) {
                            dateStmt.setInt(1, interactionId);

                            try (ResultSet dateRs = dateStmt.executeQuery()) {
                                if (dateRs.next()) {
                                    String interactionDate = dateRs.getString("interaction_date");

                                    // Create and return the Interaction object
                                    interaction = new Interaction(
                                            interactionId,
                                            user,
                                            article,
                                            interaction_type,
                                            LocalDate.parse(interactionDate)
                                    );
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Issue while connecting to the database.");
        }

        return interaction;
    }

    public Article saveNewArticle(String title, String content, Category category) {
        String sql = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the query
            pst.setString(1, title);
            pst.setString(2, content);
            pst.setString(3, category.toString());

            // Execute the update
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the generated keys
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int articleId = rs.getInt(1); // Get the generated ID
                        // Create and return a new Article object with the ID
                        return new Article(articleId, title, content, category);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null in case of failure
    }

    public boolean saveUpdatedArticle(Article article) {
        String sql = "UPDATE Articles SET title = ?, content = ? WHERE article_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            // Set values for the prepared statement
            pst.setString(1, article.getTitle());
            pst.setString(2, article.getContent());
            pst.setInt(3, article.getArticleID());

            // Execute the update
            int affectedRows = pst.executeUpdate();

            // Return true if at least one row was updated
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Issue while connecting to the database.");
        }
        return false; // Return false if an exception occurs or no rows are updated
    }


    public boolean removeDeletedArticle(int article_id) {
        String sql = "DELETE FROM Articles WHERE article_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            // Enable foreign keys for this connection (ensure cascade is applied)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            // Set the article_id parameter
            pst.setInt(1, article_id);

            // Execute the deletion and check how many rows were affected
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article with ID " + article_id + " successfully deleted.");
                return true;
            } else {
                System.out.println("No article found with ID " + article_id);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error occurred while deleting the article: " + e.getMessage());
        }
        return false;
    }


    public boolean checkUsernameAvailability(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND role = 'USER'";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // If the count is greater than 0, the username is already taken
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return false in case of an error (indicates username is unavailable)
        return false;
    }

    public void removeUser(int user_id) {
        // SQL query to delete the user based on their unique identifier (e.g., user_id or username)
        String deleteSQL = "DELETE FROM Users WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(deleteSQL)) {

            // Enable foreign keys for this connection
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            // Set the user_id parameter
            pst.setInt(1, user_id);

            // Execute the deletion
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User successfully deleted.");
            } else {
                System.out.println("No user found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Issue while connecting to the database.");
        }
    }

    public List<String> getUserDetails(String username) {
        List<String> userDetailsList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, firstname, lastname, registration_date, role FROM Users WHERE username LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username); // Support partial matches
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                userDetailsList.add(rs.getString("user_id"));
                userDetailsList.add(rs.getString("username"));
                userDetailsList.add(rs.getString("password"));
                userDetailsList.add(rs.getString("firstname"));
                userDetailsList.add(rs.getString("lastname"));
                userDetailsList.add(rs.getString("registration_date"));
                userDetailsList.add(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDetailsList; // Return the list of user details
    }

    public List<Article> fetchArticles() {
        List<Article> articleList = new ArrayList<>();
        String sql = "SELECT article_id, title, content, category FROM Articles";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("article_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String category = rs.getString("category");

                // Create a new Article object
                Article article = new Article(id, title, content, Category.valueOf(category.toUpperCase()));
                articleList.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articleList;
    }

    public void updateUserDetails(User user){
        String sql = "UPDATE Users SET username = ?, password = ?, firstname = ?, lastname = ? " +
                "WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getFirstName());
            pst.setString(4, user.getLastName());
            pst.setInt(5, user.getUserID());
            pst.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<Article> fetchFilteredArticles(Category category) {
        List<Article> articleList = new ArrayList<>();
        String sql = "SELECT article_id, title, content FROM Articles WHERE category = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, category.toString().toUpperCase()); // Set the category parameter
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Article article = new Article(
                        rs.getInt("article_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        category
                );
                articleList.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articleList; // Return the list of Article objects
    }


    public void updatePreference(int user_id, Preference preference){
        String sql = "UPDATE Preferences SET interest_level = ? WHERE user_id = ? AND category = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setInt(1, preference.getInterestLevel());
            pst.setInt(2, user_id);
            pst.setString(3, preference.getCategory().toString());
            pst.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<SystemUser> fetchRegisteredUsers() {
        List<SystemUser> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, firstname, lastname, registration_date, role FROM Users";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Create a User object for each row in the result set
                SystemUser user = new SystemUser(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        LocalDate.parse(rs.getString("registration_date")),
                        Role.valueOf(rs.getString("role"))
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Issue while connecting to the database.");
        }
        return users;
    }

    public boolean resetPassword(int user_id, String pw){
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        try(Connection conn = getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1, pw);
            pst.setInt(2, user_id);
            int rowsAffected = pst.executeUpdate(); // Returns the number of rows updated
            if (rowsAffected > 0) {
                return true; // Update was successful
            } else {
                System.out.println("User ID does not exist.");
                return false; // No rows affected
            }
        }
        catch(SQLException e){
            System.out.println("DB ISSUE");
        }
        return false;
    }

    public List<Interaction> getUserInteractions(User user, LocalDate fromDate) {
        String query = "SELECT * FROM Interactions WHERE user_id = ? AND interaction_date >= ?";
        List<Interaction> interactions = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, user.getUserID());
            pst.setDate(2, Date.valueOf(fromDate));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Article article = getArticleById(rs.getInt("article_id")); // Fetch related article
                    interactions.add(new Interaction(
                            rs.getInt("interaction_id"),
                            user, // Use the provided User object
                            article,
                            rs.getString("interaction_type"),
                            rs.getDate("interaction_date").toLocalDate() // Correctly parse the LocalDate
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interactions;
    }

    public Article getArticleById(int articleId) {
        String query = "SELECT * FROM Articles WHERE article_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, articleId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Article(
                            rs.getInt("article_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            Category.valueOf(rs.getString("category").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Preference> getUserPreferences(int userId) {
        String query = "SELECT * FROM Preferences WHERE user_id = ?";
        List<Preference> preferences = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    preferences.add(new Preference(
                            Category.valueOf(rs.getString("category").toUpperCase()),
                            rs.getInt("interest_level")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferences;
    }


}
