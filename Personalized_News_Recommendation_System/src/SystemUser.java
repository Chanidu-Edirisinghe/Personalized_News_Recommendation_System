import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SystemUser {
    private int userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate registrationDate;
    private Role role;

    public SystemUser(int userID, String username, String password, String firstname,
                      String lastname, LocalDate registrationDate, Role role){
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.registrationDate = registrationDate;
    }

    public SystemUser(int user_id, String username, String password, String firstname,
                      String lastname, LocalDate registrationDate){
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.registrationDate = LocalDate.now();
    }

    public SystemUser() {

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

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
