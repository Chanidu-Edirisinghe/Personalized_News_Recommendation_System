import java.io.*;
import java.util.*;

public class ArticleCategorizer {

    private static final String[] culture_keywords = {"ancient", "architecture", "art", "blending",
            "cultural", "culture", "cultures", "design", "digital", "diversity", "essential", "fashion",
            "festivals", "forms", "fostering", "global", "globalized", "heritage", "history", "hollywood",
            "identity", "impact", "importance", "increasingly", "influence", "influences", "language",
            "languages", "local", "media", "modern", "music", "practices", "preservation", "preserve",
            "reflection", "religion", "role", "shape", "shaping", "social", "sports", "tourism", "traditional",
            "traditions", "trends", "understanding", "unique", "values", "world"
    };

    private static final String[] education_keywords = {
            "ai", "challenges", "classrooms", "continue", "continues", "curricula", "demand", "digital",
            "edtech", "education", "educational", "emotional", "enhance", "ensure", "eq", "essential",
            "experience", "experiences", "financial", "flexible", "future", "health", "helping", "higher",
            "homeschooling", "importance", "increasingly", "international", "learning", "like", "make",
            "media", "mental", "online", "personalized", "platforms", "providing", "rise", "schools",
            "social", "startups", "stem", "student", "students", "teachers", "technology", "tools",
            "universities", "way", "world"
    };

    private static final String[] entertainment_keywords = {
            "anime", "art", "artists", "audiences", "changes", "classic", "concerts", "content",
            "creative", "digital", "diverse", "enjoying", "entertainment", "events", "experiences",
            "fans", "fashion", "festivals", "film", "films", "gaming", "genres", "high", "immersive",
            "increasingly", "independent", "industry", "interactive", "issues", "live", "major", "mobile",
            "music", "new", "offer", "performances", "platforms", "popular", "popularity", "possibilities",
            "production", "reality", "series", "shows", "specials", "storytelling", "streaming",
            "tv", "viewers", "virtual"
    };

    private static final String[] health_keywords = {
            "access", "activity", "air", "balanced", "better", "cancer", "crucial", "diet", "disease", "diseases",
            "exercise", "experts", "foods", "gut", "health", "healthcare", "heart", "help", "hydration", "importance",
            "improve", "leading", "like", "long", "making", "mental", "mood", "new", "outcomes", "overall", "physical",
            "preventive", "quality", "recommend", "reduce", "reducing", "regular", "research", "risk", "role",
            "screenings", "shown", "sleep", "smoking", "stress", "studies", "telemedicine", "term", "treatment",
            "vaccines"
    };

    private static final String[] history_keywords = {
            "age", "ancient", "art", "century", "civilization", "cultural", "death", "development",
            "economic", "egypt", "empire", "end", "europe", "exploration", "fall", "fought", "french",
            "global", "history", "impact", "including", "influence", "led", "like", "marked", "modern",
            "napoleon", "new", "period", "played", "political", "press", "printing", "profound", "revolution",
            "rise", "road", "role", "roles", "roman", "silk", "slavery", "spread",
            "states", "systems", "trade", "united", "war", "women", "world"
    };

    private static final String[] politics_keywords = {
            "address", "advocates", "aim", "argue", "balancing", "budget", "changes", "climate", "continues",
            "criminal", "crisis", "critics", "debate", "defense", "discussions", "economic", "education",
            "energy", "fiscal", "funding", "healthcare", "housing", "incentives", "increased", "justice",
            "labor", "lawmakers", "laws", "leaders", "national", "new", "opponents", "policies", "policy",
            "potential", "programs", "proponents", "proposals", "public", "recent", "reduce",
            "reform", "reforms", "remains", "renewable", "say", "security", "spending", "support", "tax"
    };

    private static final String[] science_keywords = {
            "3d", "accessible", "artificial", "challenges", "change", "climate", "computers", "conservation",
            "continues", "create", "crispr", "data", "editing", "efficient", "enabling", "energy", "fuels",
            "future", "gene", "genetic", "healthcare", "human", "improve", "industries", "life", "like", "make",
            "manufacturing", "medicine", "new", "personalized", "photosynthesis", "potential", "printing", "promise",
            "providing", "quantum", "remain", "renewable", "researchers", "role", "science", "scientific", "scientists",
            "smart", "sources", "space", "technology", "tourism", "used"
    };

    private static final String[] sport_keywords = {
            "analysts", "calling", "championship", "coach", "decision", "eagerly", "fans", "field", "filled",
            "final", "game", "games", "high", "historic", "hopeful", "impressive", "incredible", "known",
            "latest", "league", "left", "long", "looking", "major", "match", "moments", "national", "new",
            "opponent", "performance", "performances", "player", "players", "plays", "record", "season",
            "secure", "skill", "sport", "sports", "star", "stunning", "team", "teams",
            "teamâ", "trade", "underdog", "victory", "win", "young"
    };

    private static final String[] technology_keywords = {
            "5g", "advancements", "advances", "ai", "applications", "assistants", "battery",
            "blockchain", "breakthrough", "businesses", "capable", "companies", "complex",
            "computing", "concerns", "cybersecurity", "data", "devices", "energy", "experts",
            "exploration", "health", "industries", "innovation", "innovations", "investing", "language",
            "like", "making", "new", "offering", "privacy", "private", "provide", "quantum", "reality",
            "remain", "renewable", "researchers", "rpa", "safety", "security",
            "significant", "smart", "solutions", "space", "tasks", "technologies", "technology", "work"
    };

    private static final String[] weather_keywords = {
            "ai", "air", "atmosphere", "carbon", "change", "changes", "climate", "cloud", "clouds",
            "creating", "currents", "cycle", "deforestation", "earth", "effect", "effects", "el",
            "events", "extreme", "form", "gases", "global", "greenhouse", "heat", "impact", "leading",
            "lightning", "like", "meteor", "meteorologists", "niã", "occurs", "ocean", "patterns", "play",
            "pollution", "precipitation", "rainfall", "role", "significant", "storm", "storms",
            "temperatures", "thunder", "tornadoes", "vapor", "warm", "warming", "water", "weather"
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
