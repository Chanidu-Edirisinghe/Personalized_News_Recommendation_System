import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User extends SystemUser{
    private List<Preference> preferences = new ArrayList<>();
    private List<Interaction> interactions = new ArrayList<>();

    private DatabaseHandler dbh = new DatabaseHandler();

    public User(int user_id, String username, String password, String firstname, String lastname, LocalDate regDate) {
        super(user_id, username, password, firstname, lastname, regDate, Role.USER);
    }

    public User(){

    }


    public void addPreference(Preference preference){
        preferences.add(preference);
        dbh.savePreference(this.getUserID(), preference);
    }

//    public void manageProfile(){
//
//    }

    public void viewArticles(){
        System.out.println("\nView Articles selected.\n");
        DatabaseHandler dbHandler = new DatabaseHandler();
        List<List<String>> articles = dbHandler.fetchArticles();
        System.out.println("Article ID   Article Title");
        for (List<String> article : articles) {
            System.out.print(article.getFirst());
            System.out.print(" | ");
            System.out.print(article.getLast());
            System.out.println();
        }
    }

    public void updatePreferences(int prefNumber, Category category, int interest_level){
        Preference pref = preferences.get(prefNumber);
        pref.setInterestLevel(interest_level);
        dbh.updatePreference(this.getUserID(), pref);
    }

    public void recordInteraction(Interaction interaction){
        interactions.add(interaction);
        dbh.saveInteraction(interaction);
    }

    public List<Article> getRecommendations(){
        RecommendationEngine re = new RecommendationEngine();
        return re.generateRecommendations();
    }

    public void viewPreferences(){
        System.out.println(preferences);
    }

    public void viewFilteredArticles(Category category){
        System.out.println(dbh.fetchFilteredArticles(category));
    }

//    public void viewDetails(){
//        super.displayUserAccountDetails();
//    }

    public void updateDetails(String username, String password, String firstName, String lastName){
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        dbh.updateUserDetails(this);
    }
}
