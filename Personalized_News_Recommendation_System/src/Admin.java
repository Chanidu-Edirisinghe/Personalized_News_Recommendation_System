import java.time.LocalDate;

public class Admin extends SystemUser{

    private DatabaseHandler dbh = new DatabaseHandler();

    public Admin(int user_id, String username, String password, String firstName, String lastName, LocalDate regDate){
        super(user_id, username, password, firstName, lastName, regDate, Role.ADMIN);
    }

    public void deactivateUserProfile(int user_id){
        dbh.removeUser(user_id);
    }

    public void addArticle(){
        // call methods in article fetcher
    }

    public void editArticle(int article_id){

    }

    public void deleteArticle(int article_id){
        dbh.removeDeletedArticle(article_id);
    }

    public void resetPassword(){
        // use generatePassword method to generate a password and display to admin.
    }

    public void displayRegisteredUsers(){
        System.out.println(dbh.fetchRegisteredUsers());
    }

    public static void generateNewPassword(){
        // use a randomizer to generate a password
    }

}
