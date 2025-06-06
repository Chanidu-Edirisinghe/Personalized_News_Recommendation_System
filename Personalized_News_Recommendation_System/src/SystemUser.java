import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SystemUser {
    private final int userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private final LocalDate registrationDate;
    private final Role role;

    // constructor
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

    public void displayUserAccountDetails(){
        System.out.println("______________________________________________________");
        System.out.println("User ID: "+this.userID);
        System.out.println("Username: "+this.username);
        System.out.println("Password: "+this.password);
        System.out.println("Name: "+this.firstName +" "+this.lastName);
        System.out.println("Registration Date: "+this.registrationDate);
        System.out.println("______________________________________________________");
    }

    public void viewArticles(List<Article> articles){
        System.out.println("\nView Articles selected.\n");
        List<String> headers = Arrays.asList("article_id", "title", "category");
        printTable(articles, headers, article -> Arrays.asList(
                String.valueOf(article.getArticleID()),
                article.getTitle(),
                article.getCategory().toString()
        ));
    }

    // helper method for printing users
    public static <T> void printTable(List<T> data, List<String> headers, Function<T, List<String>> rowMapper) {
        if (data == null || data.isEmpty()) {
            System.out.println("No data available to display.");
            return;
        }

        // Map data to rows using the rowMapper
        List<List<String>> rows = new ArrayList<>();
        for (T item : data) {
            rows.add(rowMapper.apply(item));
        }

        // Calculate column widths
        int[] columnWidths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            columnWidths[i] = headers.get(i).length(); // Start with header lengths
        }
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                columnWidths[i] = Math.max(columnWidths[i], row.get(i).length());
            }
        }

        // Print table
        printSeparator(columnWidths);
        printRow(headers, columnWidths); // Print headers
        printSeparator(columnWidths);   // Separator after headers
        for (List<String> row : rows) {
            printRow(row, columnWidths);
        }
        printSeparator(columnWidths);   // Final separator
    }

    // helper method for table of users
    private static void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            System.out.print("-".repeat(width + 2));
        }
        System.out.println("+");
    }

    // helper method for table of users
    private static void printRow(List<String> row, int[] columnWidths) {
        for (int i = 0; i < row.size(); i++) {
            System.out.printf("| %-"+ columnWidths[i] +"s ", row.get(i));
        }
        System.out.println("|");
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
