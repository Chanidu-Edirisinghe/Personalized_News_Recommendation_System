import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Admin extends SystemUser{

    private DatabaseHandler dbh = new DatabaseHandler();

    public Admin(int user_id, String username, String password, String firstName, String lastName, LocalDate regDate){
        super(user_id, username, password, firstName, lastName, regDate, Role.ADMIN);
    }

    public void deactivateUserProfile(int user_id){
        if(dbh.addDeletedUserID(user_id)){
            dbh.removeUser(user_id);
        }
        else{
            System.out.println("Error in deleting user.");
        }
    }

    public void addArticle(){
        // call methods in article fetcher

    }

    public void editArticle(Article article){
        dbh.saveUpdatedArticle(article);
    }

    public void deleteArticle(int article_id){
        if(dbh.addDeletedArticleID(article_id)){
            dbh.removeDeletedArticle(article_id);
        }
        else{
            System.out.println("Error in deleting article.");
        }
    }

    public void resetPassword(int user_id){
        // use generatePassword method to generate a password and display to admin.
        String password = generatePassword();
        if(dbh.resetPassword(user_id, password)){
            System.out.println("New password is: "+password);
        }
    }

    private static String generatePassword(){
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?";
        String allCharacters = upperCaseLetters + lowerCaseLetters + numbers + specialCharacters;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // Fill the rest of the password with random characters
        for (int i = 4; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }
        return password.toString();
    }

    public void displayRegisteredUsers(){
        List<String> headers = Arrays.asList("user_id", "username", "password", "first name", "last name", "role");
        List<SystemUser> users = dbh.fetchRegisteredUsers();
        printTable(users, headers, user -> Arrays.asList(
                String.valueOf(user.getUserID()),
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().toString()
        ));

    }



}
