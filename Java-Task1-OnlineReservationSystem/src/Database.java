import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:reservation.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {

        String trainTable =
                "CREATE TABLE IF NOT EXISTS trains (" +
                "train_number TEXT PRIMARY KEY, " +
                "train_name TEXT NOT NULL, " +
                "source TEXT NOT NULL, " +
                "destination TEXT NOT NULL, " +
                "travel_date TEXT NOT NULL, " +
                "available_seats INTEGER NOT NULL)";

       String reservationTable =
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr TEXT PRIMARY KEY, " +
                "passenger_name TEXT NOT NULL, " +
                "train_number TEXT NOT NULL, " +
                "train_name TEXT NOT NULL, " +
                "class_type TEXT NOT NULL, " +
                "source TEXT NOT NULL, " +
                "destination TEXT NOT NULL, " +
                "travel_date TEXT NOT NULL)";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(trainTable);
            statement.execute(reservationTable);

            System.out.println("Database tables created successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}