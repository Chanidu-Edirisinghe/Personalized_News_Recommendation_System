import java.util.ArrayList;
import java.util.List;

public class User extends SystemUser{
    private List<Preference> preferences = new ArrayList<>();
    private List<Interaction> interactions = new ArrayList<>();

    public User(int user_id, String username, String password, String firstname, String lastname) {
        super(user_id, username, password, firstname, lastname, Role.USER);
    }

    public User(String username, String password, String firstname, String lastname) {
        super(username, password, firstname, lastname, Role.USER);
    }

    public User(){

    }


    public void addPreference(Preference preference){
        preferences.add(preference);
    }

    public boolean register() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();

        // Add the user to the database
        boolean isAdded = dbHandler.saveNewUser(this);
        // get id from db and set to object
        this.setUserID(Integer.parseInt(dbHandler.getUserDetails(this.getUsername()).getFirst()));
        dbHandler.closeConnection();

        return isAdded;

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

    public void updatePreferences(){

    }

    public void recordInteraction(){

    }

    public void getRecommendations(){

    }

    public void viewPreferences(){

    }

    public void viewFilteredArticles(){

    }

    public void viewDetails(){

    }

    public void updateDetails(){

    }
}
