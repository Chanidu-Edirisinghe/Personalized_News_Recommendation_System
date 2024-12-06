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
        // Interactions used only if the daysSinceRegistration > 7
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
                    categoryUpdates.put(category, categoryUpdates.get(category) + 5);
                    break;
                case "Read":
                    categoryUpdates.put(category, categoryUpdates.get(category) + 1);
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
                preference.setInterestLevel(updatedLevel);
            }
        }

        // decay used to slow down the speed at which the upper bound is reached
        double decayRate = 0.1; // 10% decay
        for (Preference preference : preferences) {
            int decayedLevel = (int) (preference.getInterestLevel() * (1 - decayRate));
            preference.setInterestLevel(decayedLevel);
        }

        // Step 6: Update preferences in DB
        for (Preference preference : preferences) {
            dbh.updatePreference(preference); // Calls the updatePreference method for each preference
        }
    }


}
