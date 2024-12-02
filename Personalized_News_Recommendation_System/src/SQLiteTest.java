import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class SQLiteTest {


    // Method to establish a connection to the database
    public static Connection connectToDB(String url) {
        try {
            // Establishing connection
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to create tables in the database
    public static void makeTables(Connection conn) {
        // SQL statement to create a table

        String createUsersTableSQL = "CREATE TABLE Users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "firstname TEXT NOT NULL, "
                + "lastname TEXT NOT NULL, "
                + "registration_date DATE DEFAULT (DATE('now')), "
                + "role TEXT DEFAULT 'User' CHECK(role IN ('USER', 'ADMIN'))"
                + ");";

        String createArticlesTableSQL = "CREATE TABLE Articles ("
                + "article_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "content TEXT NOT NULL, "
                + "category TEXT NOT NULL"
                + ");";


        String createInteractionsTableSQL = "CREATE TABLE Interactions ("
                + "interaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "article_id INTEGER NOT NULL, "
                + "interaction_type TEXT CHECK(interaction_type IN ('Read', 'Like', 'Skip')) NOT NULL, "
                + "interaction_date DATE DEFAULT (DATE('now')), "
                + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
                + ");";


        String createPreferencesTableSQL = "CREATE TABLE Preferences ("
                + "preference_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "category TEXT NOT NULL, "
                + "interest_level INTEGER DEFAULT 5 CHECK(interest_level BETWEEN 0 AND 100), "
                + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE"
                + ");";




        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement to create the table
            stmt.executeUpdate(createUsersTableSQL);
            stmt.executeUpdate(createArticlesTableSQL);
            stmt.executeUpdate(createInteractionsTableSQL);
            stmt.executeUpdate(createPreferencesTableSQL);

            System.out.println("Tables have been created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Define the database URL (SQLite will create the file if it doesn't exist)
        String url = "jdbc:sqlite:news_recommendation_system.db";

        // Connect to the database
        Connection conn = connectToDB(url);

        if (conn != null) {
            // Create tables if the connection is successful
            //makeTables(conn);
            String folderPath = "C:\\Users\\User\\Documents\\IIT\\AIDS Degree Details\\Y2S1\\CM2601 - Object Orientated Development\\Coursework\\CW\\Articles";

            // Create a File object for the folder
            File folder = new File(folderPath);

            // Ensure the folder exists and is a directory
            if (folder.exists() && folder.isDirectory()) {
                // Get a list of all .json files in the folder
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

                // Loop through each file and call addArticle
                if (files != null) {
                    for (File file : files) {
                        // Get the file path
                        String filePath = file.getAbsolutePath();

                        // Get the file name without the extension
                        String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));

                        // Call the addArticle function
                        addArticle(filePath, fileName, conn); // Replace "conn" with your actual connection object
                    }
                }
            } else {
                System.out.println("The specified folder does not exist or is not a directory.");
            }
            
            // Close the connection
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addArticle(String filepath, String category, Connection conn){
        String insertSQL = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(insertSQL)) {
            // Read and parse the JSON file
            String content = new String(Files.readAllBytes(Paths.get(filepath)));
            JSONArray jsonArray = new JSONArray(content);

            // Iterate through the JSON array and insert articles
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String articleContent = jsonObject.getString("content");

                // Set parameters for the SQL query
                pst.setString(1, title);
                pst.setString(2, articleContent);
                pst.setString(3, category.toUpperCase());

                // Add the statement to the batch
                pst.addBatch();
            }

            // Execute the batch
            pst.executeBatch();

            System.out.println("Articles inserted successfully.");
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error inserting articles: ", e);
        }
    }


    public static void readArticles() {
        // Path to the JSON file
        String jsonFilePath = "C:\\Users\\User\\Documents\\IIT\\AIDS Degree Details\\Y2S1\\CM2601 - Object Orientated Development\\Coursework\\WebScraper\\ArticleClassification\\culture.json";
        try {
            // Read JSON file as a string
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

            // Parse JSON string into JSONObject
            // Parse JSON content as an array
            JSONArray jsonArray = new JSONArray(content);

            // Iterate through the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("title");
                String articleContent = jsonObject.getString("content");

                System.out.println("Title: " + title);
                System.out.println("Content: " + articleContent);
                System.out.println("-------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
