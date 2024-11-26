import java.util.ArrayList;
import java.util.List;

public class User extends SystemUser{
    private List<Preference> preferences = new ArrayList<>();
    private List<Interaction> interactions = new ArrayList<>();

    private DatabaseHandler dbh = new DatabaseHandler();

    public User(int user_id, String username, String password, String firstname, String lastname) {
        super(user_id, username, password, firstname, lastname, Role.USER);
    }

    public User(){

    }


    public void addPreference(Preference preference){
        preferences.add(preference);
        dbh.connect();
        dbh.savePreference(this.getUserID(), preference);
        dbh.closeConnection();
    }

    public static int register(String username, String password, String firstname, String lastname) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();
        int user_id = dbHandler.saveNewUser(username, password, firstname, lastname);
        dbHandler.closeConnection();

        return user_id;

    }

    public void manageProfile(){
        System.out.println("--------User Profile--------");
        System.out.println("Username: "+this.getUsername());
        System.out.println("Password: "+this.getRegistrationDate());

    }

    public void viewArticles(){
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();
        List<List<String>> articles = dbHandler.fetchArticles();
        System.out.println("Article ID   Article Title");
        for (List<String> article : articles) {
            System.out.print(article.getFirst());
            System.out.print(" | ");
            System.out.print(article.getLast());
            System.out.println();
        }
        dbHandler.closeConnection();
    }

    public void updatePreferences(int prefNumber, Category category, int interest_level){
        Preference pref = preferences.get(prefNumber);
        pref.setInterestLevel(interest_level);
        dbh.connect();
        dbh.updatePreference(this.getUserID(), pref);
        dbh.closeConnection();
    }

    public void recordInteraction(Interaction interaction){
        dbh.connect();
        interactions.add(interaction);
        dbh.saveInteraction(interaction);
        dbh.closeConnection();
    }

    public List<Article> getRecommendations(){
        RecommendationEngine re = new RecommendationEngine();
        return re.generateRecommendations();
    }

    public void viewPreferences(){
        System.out.println(preferences);
    }

    public void viewFilteredArticles(Category category){
        dbh.connect();
        System.out.println(dbh.fetchFilteredArticles(category));
        dbh.closeConnection();
    }

//    public void viewDetails(){
//        super.displayUserAccountDetails();
//    }

    public void updateDetails(String username, String password, String firstName, String lastName){
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        dbh.connect();
        dbh.updateUserDetails(this);
        dbh.closeConnection();
    }
}
