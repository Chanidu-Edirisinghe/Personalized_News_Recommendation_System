public class Admin extends SystemUser{

    private DatabaseHandler dbh = new DatabaseHandler();

    public Admin(int user_id, String username, String password, String firstName, String lastName){
        super(user_id, username, password, firstName, lastName, Role.ADMIN);
    }

    public void manageUserAccounts(){

    }

    public void manageArticles(){

    }

    public void deactivateUserProfile(int user_id){
        dbh.connect();
        dbh.removeUser(user_id);
        dbh.closeConnection();
    }

    public void addArticle(){
        // call methods in article fetcher
    }

    public void editArticle(int article_id){

    }

    public void deleteArticle(int article_id){
        dbh.connect();
        dbh.removeDeletedArticle(article_id);
        dbh.closeConnection();
    }

    public void resetPassword(){
        // use generatePassword method to generate a password and display to admin.
    }

    public void displayRegisteredUsers(){
        dbh.connect();
        System.out.println(dbh.fetchRegisteredUsers());
        dbh.closeConnection();
    }

    public static void generateNewPassword(){
        // use a randomizer to generate a password
    }

}
