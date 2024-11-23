import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        SystemUser user1 = new SystemUser("001", "ccchcch", "chanidu223344",
                "Chanidu", "Edirisinghe", LocalDate.now(), Role.ADMIN);

        System.out.println(user1.login("ccchcch", "chanidu223344"));

    }
}