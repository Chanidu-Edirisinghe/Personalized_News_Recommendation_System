import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Scanner;

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


        String createKeywordsTableSQL = "CREATE TABLE Keywords ("
                + "keyword_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "article_id INTEGER NOT NULL, "
                + "keyword TEXT NOT NULL, "
                + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
                + ");";

        String createInteractionsTableSQL = "CREATE TABLE Interactions ("
                + "interaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "article_id INTEGER NOT NULL, "
                + "interaction_type TEXT CHECK(interaction_type IN ('Read', 'Like', 'Skip')) NOT NULL, "
                + "interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
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

        String recycledUserIDs = "CREATE TABLE RecycledUserIDs ("
                +"user_id INTEGER PRIMARY KEY);";

        String recycledArticleIDs = "CREATE TABLE RecycledArticleIDs ("
                +"article_id INTEGER PRIMARY KEY);";



        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statement to create the table
//            stmt.executeUpdate(createUsersTableSQL);
//            stmt.executeUpdate(createArticlesTableSQL);
//            stmt.executeUpdate(createKeywordsTableSQL);
//            stmt.executeUpdate(createInteractionsTableSQL);
//            stmt.executeUpdate(createPreferencesTableSQL);
            stmt.executeUpdate(recycledArticleIDs);
            System.out.println("Tables have been created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTableData(Connection conn){
        String[] articles = {
                "('The Evolution of Global Fashion Trends', 'Fashion has evolved dramatically over the years, with trends shifting based on cultural, political, and technological influences. From the elegant styles of the Victorian era to the casual and bold looks of modern streetwear, fashion has always been a reflection of societal values. In recent years, sustainability and inclusivity have become key drivers in the fashion industry, as consumers demand more eco-friendly and diverse options. Fashion shows and influencers continue to shape what’s considered trendy, but the power of individual style is now more prominent than ever.', 'Culture')",
                "('The Importance of Traditional Music in Culture', 'Traditional music plays a significant role in preserving cultural identity and heritage. It serves as a means of storytelling, passing down historical events, beliefs, and values from one generation to the next. Each region and community has its own unique sound, instruments, and rhythms that reflect their way of life. In today’s globalized world, the fusion of traditional music with modern genres helps to keep cultural practices alive while attracting younger generations. This blending of old and new also promotes cultural exchange and understanding.', 'Culture')",
                "('The Role of Language in Shaping Cultural Identity', 'Language is more than just a tool for communication; it is a key component of cultural identity. It reflects the values, beliefs, and history of a community. Many cultures have their own unique languages or dialects that distinguish them from others. The loss of a language can mean the loss of cultural heritage, as much of a culture’s traditions, myths, and expressions are embedded in language. Efforts to preserve endangered languages are vital to maintaining cultural diversity and fostering a deeper understanding of human history.', 'Culture')",
                "('The Influence of Ancient Architecture on Modern Design', 'Ancient architecture has had a profound influence on modern design. Structures like the Greek Parthenon, Roman aqueducts, and Egyptian pyramids showcase timeless engineering techniques that continue to inspire architects today. Modern buildings often draw on classical elements, such as columns, arches, and symmetry, integrating them with contemporary materials and technology. The influence of ancient architecture is seen in iconic structures worldwide, from government buildings to cultural monuments, illustrating how the past shapes the future of design and construction.', 'Culture')",
                "('The Global Impact of Hollywood on Pop Culture', 'Hollywood has long been a dominant force in global pop culture. American films and television shows have reached audiences worldwide, shaping perceptions of fashion, language, and lifestyle. Hollywood stars often become global icons, and their influence extends beyond the silver screen. The success of Hollywood also promotes the dominance of English as a global language and fosters international tourism, especially in California. While the film industry is increasingly globalized, Hollywood’s legacy as the epicenter of entertainment continues to shape the cultural landscape.', 'Culture')",
                "('Cultural Significance of Traditional Festivals', 'Traditional festivals are an essential part of cultural identity, providing communities with an opportunity to celebrate their heritage and express collective values. These festivals, which often center around religious or seasonal themes, include rituals, music, dance, and food. From Diwali in India to Oktoberfest in Germany, traditional festivals connect people with their history, foster a sense of belonging, and provide an avenue for cultural exchange. They also serve as a reminder of the importance of preserving cultural practices amidst modern global influences.', 'Culture')",
                "('The Rise of Digital Art and Its Cultural Impact', 'Digital art has emerged as a powerful form of creative expression in the 21st century. With the advent of advanced software and technology, artists can now create stunning visual works that exist solely in the digital realm. This has opened up new possibilities for artistic innovation, allowing for interactive art and virtual experiences. As digital art gains popularity, it is reshaping the art world by challenging traditional boundaries and raising questions about authorship, authenticity, and the role of technology in the creative process.', 'Culture')",
                "('The Importance of Cultural Preservation in a Globalized World', 'As the world becomes increasingly interconnected, the preservation of cultural practices, languages, and traditions has become a critical concern. Globalization has led to the blending of cultures, but it also poses a threat to smaller, indigenous cultures that risk being overshadowed. Efforts to preserve cultural heritage are essential to maintaining diversity and understanding. Organizations and governments around the world are working to protect endangered languages, traditional arts, and cultural monuments to ensure that future generations can experience and appreciate cultural diversity.', 'Culture')",
                "('The Role of Sports in Shaping Cultural Values', 'Sports play a significant role in shaping cultural values, fostering national pride, and bringing communities together. From the Olympics to local sporting events, sports are a reflection of societal values such as teamwork, discipline, and perseverance. In some cultures, sports are a central part of daily life, and athletes are seen as heroes. The media’s portrayal of sports figures also influences societal norms and expectations. As a result, sports help to shape both individual identity and collective cultural narratives.', 'Culture')",
                "('Cuisine as a Reflection of Cultural Heritage', 'Food is an integral part of cultural heritage, with each cuisine offering a unique insight into the history, geography, and traditions of a particular culture. Traditional dishes often tell the story of a community’s resources, agricultural practices, and social customs. For example, Italian pasta, Japanese sushi, and Mexican tacos are more than just meals; they are expressions of cultural pride and identity. As global travel and migration increase, culinary traditions are exchanged and adapted, creating a rich tapestry of multicultural influences.', 'Culture')",
                "('The Influence of Religion on Cultural Practices', 'Religion has a profound impact on cultural practices, shaping the way people live, celebrate, and interact with each other. Religious beliefs influence everything from holiday celebrations to dietary restrictions, clothing, and rituals. For example, Islamic culture is reflected in the practice of fasting during Ramadan, while Christian culture often centers around Christmas and Easter traditions. Religion also informs art, music, and architecture, as many of the world’s most famous cultural landmarks were built for religious purposes. The intersection of religion and culture is complex, but essential to understanding human societies.', 'Culture')",
                "('The Impact of Social Media on Cultural Identity', 'Social media has transformed how individuals express and shape their cultural identities. Platforms like Instagram, TikTok, and Twitter allow people to share their culture with a global audience, fostering cross-cultural communication and understanding. However, the widespread use of social media also raises concerns about cultural appropriation, the spread of misinformation, and the pressure to conform to global trends. While social media offers opportunities for cultural expression, it also challenges traditional notions of cultural identity by blending local customs with global influences.', 'Culture')",
                "('The Revival of Ancient Art Forms in Contemporary Culture', 'In recent years, there has been a growing interest in reviving ancient art forms, such as traditional pottery, weaving, and calligraphy. Many artists are looking to the past for inspiration, blending ancient techniques with modern aesthetics. This revival not only helps to preserve these art forms but also fosters a deeper connection to history and heritage. As a result, ancient art is gaining recognition in contemporary galleries and exhibitions, bringing attention to the importance of cultural preservation in the modern world.', 'Culture')",
                "('The Changing Landscape of Cultural Tourism', 'Cultural tourism, where travelers explore the cultural heritage of a region, is becoming an increasingly popular form of tourism. From historic landmarks to art galleries and cultural festivals, cultural tourism provides an opportunity to experience the unique traditions and values of different communities. However, the growth of cultural tourism also brings challenges, such as the risk of commodifying culture and the impact of mass tourism on local traditions and environments. Sustainable cultural tourism aims to balance economic benefits with respect for local culture and traditions.', 'Culture')"
        };
        if (conn != null) {
            String insertSQL = "INSERT INTO Articles (title, content, category) VALUES (?, ?, ?)";

            try (PreparedStatement pst = conn.prepareStatement(insertSQL)) {
                for (String article : articles) {
                    // Split the article details
                    String[] parts = article.split(",", 3);
                    pst.setString(1, parts[0].substring(1, parts[0].length() - 1)); // Title
                    pst.setString(2, parts[1].substring(1, parts[1].length() - 1)); // Content
                    pst.setString(3, parts[2].substring(1, parts[2].length() - 1)); // Category

                    pst.addBatch(); // Add to batch
                }
                pst.executeBatch(); // Execute all insert statements in the batch
                System.out.println("Articles inserted successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
            String filepath = "C:\\Users\\User\\Documents\\IIT\\AIDS Degree Details\\Y2S1\\CM2601 - Object Orientated Development\\Coursework\\WebScraper\\ArticleClassification\\culture.json";
            addArticle(filepath, "culture", conn);
            
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
                pst.setString(3, category);

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


//String createUsersTableSQL = "CREATE TABLE Users ("
//        + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//        + "username TEXT UNIQUE NOT NULL, "
//        + "password TEXT NOT NULL, "
//        + "firstname TEXT NOT NULL, "
//        + "lastname TEXT NOT NULL, "
//        + "registration_date DATE DEFAULT (DATE('now')), "
//        + "role TEXT DEFAULT 'User' CHECK(role IN ('USER', 'ADMIN'))"
//        + ");";

//String createArticlesTableSQL = "CREATE TABLE Articles ("
//        + "article_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//        + "title TEXT NOT NULL, "
//        + "content TEXT NOT NULL, "
//        + "category TEXT NOT NULL"
//        + ");";
//

//String createKeywordsTableSQL = "CREATE TABLE Keywords ("
//        + "keyword_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//        + "article_id INTEGER NOT NULL, "
//        + "keyword TEXT NOT NULL, "
//        + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
//        + ");";

//String createInteractionsTableSQL = "CREATE TABLE Interactions ("
//        + "interaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//        + "user_id INTEGER NOT NULL, "
//        + "article_id INTEGER NOT NULL, "
//        + "interaction_type TEXT CHECK(interaction_type IN ('Read', 'Like', 'Skip')) NOT NULL, "
//        + "interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
//        + "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, "
//        + "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE"
//        + ");";


//CREATE TABLE Preferences (
//        preference_id INTEGER PRIMARY KEY AUTOINCREMENT,
//        user_id INTEGER NOT NULL,
//        category TEXT NOT NULL,
//        interest_level INTEGER DEFAULT 5 CHECK(interest_level BETWEEN 0 AND 100),
//FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
//);

