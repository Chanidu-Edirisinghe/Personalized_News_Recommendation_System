import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class SystemUser {
    private String userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate registrationDate;
    private Role role;

    public SystemUser(String userID, String username, String password, String firstname,
                      String lastname, LocalDate regDate, Role role){
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.registrationDate = regDate;
    }

    public boolean login(String username, String password){
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();
        boolean result = dbHandler.authenticate(username, password);
        dbHandler.closeConnection();
        return result;

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

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}
