import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class SystemUser {
    private int userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate registrationDate;
    private Role role;

    public SystemUser(int userID, String username, String password, String firstname,
                      String lastname, Role role){
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.registrationDate = LocalDate.now();
    }

    public SystemUser() {

    }

    public boolean login(String username, String password){
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();
        boolean result = dbHandler.authenticate(username, password);
        dbHandler.closeConnection();
        return result;
    }

    public void displayUserAccountDetails(){
        System.out.println("______________________________________________________");
        System.out.println("User ID: "+this.userID);
        System.out.println("Username: "+this.username);
        System.out.println("Password: "+this.password);
        System.out.println("Name: "+this.firstName +" "+this.lastName);
        System.out.println("Registration Date: "+this.registrationDate);
        System.out.println("______________________________________________________");
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public Role getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}
