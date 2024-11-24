
public class User extends SystemUser{
    public User(int userID, String username, String password, String firstname, String lastname) {
        super(userID, username, password, firstname, lastname, Role.USER);
    }

    public boolean register() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.connect();

        // Check if the username is available
        if (!dbHandler.checkUsernameAvailability(this.getUsername())) {
            System.out.println("Username is already taken. Please choose a different one.");
            dbHandler.closeConnection();
            return false;
        }

        // Add the user to the database
        boolean isAdded = dbHandler.saveNewUser(this);
        dbHandler.closeConnection();

        if (isAdded) {
            System.out.println("Registration successful.");
            return true;
        } else {
            System.out.println("Registration failed. Please try again.");
            return false;
        }
    }

}
