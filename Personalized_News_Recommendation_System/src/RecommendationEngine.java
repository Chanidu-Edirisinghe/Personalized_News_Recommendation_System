import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RecommendationEngine {
    public static List<Article> generateRecommendations(User user) {
        DatabaseHandler dbh = new DatabaseHandler();
        // Step 1: Update user preferences
        updatePreferences(user);

        // Step 2: Sort preferences by interest level in descending order
        List<Preference> sortedPreferences = dbh.getUserPreferences(user).stream()
                .sorted((p1, p2) -> Integer.compare(p2.getInterestLevel(), p1.getInterestLevel()))
                .toList();

        // Step 3: Select the top 3 categories
        List<Category> topCategories = sortedPreferences.stream()
                .limit(3)
                .map(Preference::getCategory)
                .toList();

        List<Article> recommendedArticles = new ArrayList<>();

        // Step 4: Fetch and randomly select articles from top categories
        int[] articlesPerCategory = {3, 2, 1}; // Number of articles to pick per category
        for (int i = 0; i < topCategories.size(); i++) {
            Category category = topCategories.get(i);
            List<Article> categoryArticles = dbh.fetchFilteredArticles(category);

            // Randomly select the required number of articles from this category
            Collections.shuffle(categoryArticles); // Shuffle the list
            recommendedArticles.addAll(
                    categoryArticles.stream()
                            .limit(articlesPerCategory[i])
                            .toList()
            );
        }

        return recommendedArticles; // Return the final list of recommendations
    }

    public static void updatePreferences(User user) {
        DatabaseHandler dbh = new DatabaseHandler();
        LocalDate today = LocalDate.now();

        // Step 1: Fetch user's registration date
        LocalDate registrationDate = LocalDate.parse(dbh.getUserDetails(user.getUsername()).get(5));

        // Step 2: Calculate time difference
        long daysSinceRegistration = ChronoUnit.DAYS.between(registrationDate, today);

        // Step 3: Fetch interactions and preferences
        List<Interaction> interactions = new ArrayList<>();
        if (daysSinceRegistration > 7) {
            LocalDate oneWeekAgo = today.minusDays(7);
            interactions = dbh.getUserInteractions(user, oneWeekAgo);
        }

        List<Preference> preferences = dbh.getUserPreferences(user);

        // Step 4: Update preferences based on interactions
        Map<String, Integer> categoryUpdates = new HashMap<>();
        for (Interaction interaction : interactions) {
            String category = String.valueOf(interaction.getArticle().getCategory());
            categoryUpdates.putIfAbsent(category, 0);

            switch (interaction.getInteractionType()) {
                case "Like":
                    categoryUpdates.put(category, categoryUpdates.get(category) + 10);
                    break;
                case "Read":
                    categoryUpdates.put(category, categoryUpdates.get(category) + 5);
                    break;
                case "Skip":
                    categoryUpdates.put(category, categoryUpdates.get(category) - 5);
                    break;
            }
        }

        // Step 5: Update each category's interest level with updates
        for (Preference preference : preferences) {
            if (categoryUpdates.containsKey(String.valueOf(preference.getCategory()))) {
                int updatedLevel = preference.getInterestLevel() + categoryUpdates.get(String.valueOf(preference.getCategory()));
                preference.setInterestLevel(updatedLevel); // Clamp within 0 to 100
            }
        }

        // Step 5.1: Find min and max interest levels
        int minLevel = preferences.stream()
                .mapToInt(Preference::getInterestLevel)
                .min()
                .orElse(0);

        int maxLevel = preferences.stream()
                .mapToInt(Preference::getInterestLevel)
                .max()
                .orElse(100);

        // Step 5.2: Normalize interest levels to fit between 0 and 100
        if (minLevel != maxLevel) {
            for (Preference preference : preferences) {
                int normalizedLevel = (int) Math.round(
                        ((double) (preference.getInterestLevel() - minLevel) * 100) / (maxLevel - minLevel)
                );
                preference.setInterestLevel(normalizedLevel);
            }
        } else {
            // If all levels are the same, set all to 50
            for (Preference preference : preferences) {
                preference.setInterestLevel(50);
            }
        }

        // Step 8: Update preferences in DB
        for (Preference preference : preferences) {
            dbh.updatePreference(preference); // Calls the updatePreference method for each preference
        }
    }

    //    public void updatePreferences(User user){
//        // check user registration date and today date,
//        // if the difference is less than 1 week use preferences tied to the user
//        // if the difference is greater than 1 week , get the interactions of the user for the past week(from today)
//        // then make an algorithm that will update the preferences of the user based on the user's interactions with
//        // articles. The user can read, like or skip each article and all these interactions are stored in the db.
//        // After getting user interactions for past week, each interaction is tied to an article so can get category
//        // of each article, based on user interaction, example, if user likes an article that belongs to Technology
//        // category, update the interest level(limits of interest level is 0 - 100) in preference object by adding a
//        // suitable amount per like in the past week, per read in last week and subtracting per skip. Also make sure
//        // interest level stays between 0 and 100 and if some category approaches 100, reduce the interest level of
//        // all categories by the same amount to maintain relative importance and also to adhere to constraints.
//        // To have access to interactions of user and preferences of user, fetch them from the db using user_id
//        // and store them in the user's private List<Preference> preferences = new ArrayList<>();
//        // and  private List<Interaction> interactions = new ArrayList<>();
//    }

}
