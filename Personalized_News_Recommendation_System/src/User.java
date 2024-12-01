import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User extends SystemUser{
    private List<Preference> preferences = new ArrayList<>();
    private List<Interaction> interactions = new ArrayList<>();

    private DatabaseHandler dbh = new DatabaseHandler();

    public User(int user_id, String username, String password, String firstname, String lastname, LocalDate regDate) {
        super(user_id, username, password, firstname, lastname, regDate, Role.USER);
    }

    public void addPreference(Preference preference, boolean notAdded){
        preferences.add(preference);
        if(notAdded) {
            dbh.savePreference(this.getUserID(), preference);
        }
    }

    public void updatePreferences(int prefNumber, int interest_level){
        Preference pref = preferences.get(prefNumber);
        pref.setInterestLevel(interest_level);
        dbh.updatePreference(this.getUserID(), pref);
    }

    public void recordInteraction(User user, Article article, String interaction_type){
        Interaction interaction = dbh.saveInteraction(user, article, interaction_type);
        interactions.add(interaction);
    }

    public List<Article> getRecommendations(){
        RecommendationEngine re = new RecommendationEngine();
        return re.generateRecommendations(this);
    }

    public void viewPreferences(){
        System.out.println(preferences);
    }

    public void viewFilteredArticles(Category category){
        System.out.println(dbh.fetchFilteredArticles(category));
    }

    public void updateDetails(String username, String password, String firstName, String lastName){
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        dbh.updateUserDetails(this);
    }

}
