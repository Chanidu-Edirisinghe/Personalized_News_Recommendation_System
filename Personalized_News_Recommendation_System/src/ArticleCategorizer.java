import java.io.*;
import java.util.*;

public class ArticleCategorizer {

    private static final String[] culture_keywords = {"ancient", "art", "cultural", "culture", "global",
            "heritage", "identity", "language", "modern", "practices", "tourism",
            "traditional", "traditions", "values", "world"
    };

    private static final String[] education_keywords = {
            "challenges", "classrooms", "digital", "education", "educational",
            "financial", "future", "learning", "platforms", "schools", "students",
            "teachers", "technology", "world"
    };

    private static final String[] entertainment_keywords = {
            "anime", "art", "audiences", "fans", "industry", "mobile", "new", "platforms",
            "popularity", "production", "series", "shows", "streaming", "viewers", "virtual"
    };

    private static final String[] health_keywords = {
            "air", "disease", "diseases", "exercise", "experts", "health", "heart",
            "importance", "improve", "like", "mental", "physical", "quality", "risk",
            "stress"
    };

    private static final String[] history_keywords = {
            "century", "empire", "end", "europe", "history", "impact", "including", "led",
            "modern", "political", "revolution", "role", "roman", "war", "world"
    };

    private static final String[] politics_keywords = {
            "address", "argue", "budget", "climate", "debate", "economic", "energy",
            "funding", "healthcare", "housing", "incentives", "lawmakers", "policies",
            "reform", "tax"
    };

    private static final String[] science_keywords = {
            "artificial", "change", "climate", "data", "energy", "genetic", "life", "like",
            "medicine", "potential", "renewable", "science", "space", "technology", "used"
    };

    private static final String[] sport_keywords = {
            "analysts", "fans", "game", "high", "league", "match", "player", "players",
            "record", "season", "sport", "team", "teams", "victory", "win"
    };

    private static final String[] technology_keywords = {
            "ai", "applications", "companies", "data", "devices", "energy", "experts",
            "health", "industries", "like", "making", "new", "security", "smart", "technology"
    };

    private static final String[] weather_keywords = {
            "air", "atmosphere", "climate", "clouds", "currents", "cycle", "earth", "global",
            "leading", "niña", "patterns", "rainfall", "temperatures", "water", "weather"
    };


    public static String categorizeArticles(String article) {
        try {
            // Path to Python script
            String pythonScript = "C:\\Users\\User\\Documents\\IIT\\AIDS Degree Details\\Y2S1\\CM2601 - Object Orientated Development\\Coursework\\CW\\Personalized_News_Recommendation_System\\python_scripts\\keyword_extractor.py";

            // Call the Python script
            ProcessBuilder pb = new ProcessBuilder("python", pythonScript, article);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Python script encountered an error.");
                return null;
            }

            // Get output (keywords) from Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine(); // Assuming keywords are returned as comma-separated values
            if (line == null || line.isEmpty()) {
                System.out.println("No keywords extracted.");
                return null;
            }
            String[] extractedKeywords = line.split(",");

            // Count keyword matches for each category
            Map<String, Integer> categoryCounts = new HashMap<>();
            categoryCounts.put("Culture", countMatches(extractedKeywords, culture_keywords));
            categoryCounts.put("Education", countMatches(extractedKeywords, education_keywords));
            categoryCounts.put("Entertainment", countMatches(extractedKeywords, entertainment_keywords));
            categoryCounts.put("Health", countMatches(extractedKeywords, health_keywords));
            categoryCounts.put("History", countMatches(extractedKeywords, history_keywords));
            categoryCounts.put("Politics", countMatches(extractedKeywords, politics_keywords));
            categoryCounts.put("Science", countMatches(extractedKeywords, science_keywords));
            categoryCounts.put("Sport", countMatches(extractedKeywords, sport_keywords));
            categoryCounts.put("Technology", countMatches(extractedKeywords, technology_keywords));
            categoryCounts.put("Weather", countMatches(extractedKeywords, weather_keywords));

            // Determine the category with the highest count
            Map.Entry<String, Integer> map1 = Collections.max(categoryCounts.entrySet(), Map.Entry.comparingByValue());
            String bestCategory = map1.getKey();
            if(map1.getValue() == 0){
                System.out.println("The article doesn't belong to any category. Added to other.");
                return "other";
            }
            else{
                System.out.println("The article belongs to the category: " + bestCategory);
                return bestCategory;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int countMatches(String[] extractedKeywords, String[] categoryKeywords) {
        int count = 0;
        Set<String> categoryKeywordSet = new HashSet<>(Arrays.asList(categoryKeywords));
        for (String keyword : extractedKeywords) {
            if (categoryKeywordSet.contains(keyword)) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        DatabaseHandler dbh = new DatabaseHandler();
        List<Article> articles = dbh.fetchArticles();
        int count = 0;
        for(Article article: articles){
            String predCategory = categorizeArticles(article.getTitle()+" "+article.getContent());
            assert predCategory != null;
            if(article.getCategory().toString().equals(predCategory.toUpperCase())){
                count++;
            }
        }
        double accuracy = (double) (count * 100) /articles.size();
        System.out.println("Accuracy: "+accuracy);
    }
}
