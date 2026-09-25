import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class ReservationFrame extends JFrame {

    private JTable trainTable;
    private DefaultTableModel tableModel;

    private JTextField passengerField;
    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JComboBox<String> classTypeComboBox;
    private JTextField travelDateField;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField pnrField;

    public ReservationFrame() {

        setTitle("Online Reservation System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel title = new JLabel(
                "ONLINE RESERVATION SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(title, BorderLayout.NORTH);

        // Train table
        String[] columns = {
                "Train No.",
                "Train Name",
                "Source",
                "Destination",
                "Date",
                "Seats"
        };

        tableModel = new DefaultTableModel(columns, 0);
        trainTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(trainTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Booking form
        JPanel bookingPanel = new JPanel(
                new GridLayout(5, 4, 8, 8)
        );

        bookingPanel.setBorder(
                BorderFactory.createTitledBorder("Reservation Form")
        );

        passengerField = new JTextField();
        trainNumberField = new JTextField();

        trainNameField = new JTextField();
        trainNameField.setEditable(false);

        classTypeComboBox = new JComboBox<>(
                new String[]{
                        "General",
                        "Sleeper",
                        "AC"
                }
        );

        travelDateField = new JTextField();
        sourceField = new JTextField();
        destinationField = new JTextField();

        sourceField.setEditable(false);
        destinationField.setEditable(false);

        bookingPanel.add(new JLabel("Passenger Name:"));
        bookingPanel.add(passengerField);

        bookingPanel.add(new JLabel("Train Number:"));
        bookingPanel.add(trainNumberField);

        bookingPanel.add(new JLabel("Train Name:"));
        bookingPanel.add(trainNameField);

        bookingPanel.add(new JLabel("Class Type:"));
        bookingPanel.add(classTypeComboBox);

        bookingPanel.add(new JLabel("Date of Journey:"));
        bookingPanel.add(travelDateField);

        bookingPanel.add(new JLabel("Source:"));
        bookingPanel.add(sourceField);

        bookingPanel.add(new JLabel("Destination:"));
        bookingPanel.add(destinationField);

        JButton bookButton = new JButton("Book Ticket");
        bookingPanel.add(new JLabel());
        bookingPanel.add(bookButton);

        // Cancellation section
        JPanel cancelPanel = new JPanel();

        cancelPanel.setBorder(
                BorderFactory.createTitledBorder("Cancellation")
        );

        cancelPanel.add(new JLabel("PNR:"));

        pnrField = new JTextField(12);
        cancelPanel.add(pnrField);

        JButton viewButton = new JButton("Fetch Reservation");
        JButton cancelButton = new JButton("Confirm Cancellation");

        cancelPanel.add(viewButton);
        cancelPanel.add(cancelButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        bottomPanel.add(bookingPanel, BorderLayout.CENTER);
        bottomPanel.add(cancelPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Database
        createDatabase();
        loadTrains();

        // Train table selection
        trainTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int selectedRow = trainTable.getSelectedRow();

                if (selectedRow != -1) {

                    trainNumberField.setText(
                            tableModel.getValueAt(
                                    selectedRow, 0
                            ).toString()
                    );

                    loadTrainDetails();
                }
            }
        });

        // Buttons
        trainNumberField.addActionListener(e -> loadTrainDetails());

        bookButton.addActionListener(e -> bookTicket());

        viewButton.addActionListener(e -> viewReservation());

        cancelButton.addActionListener(e -> cancelTicket());
    }

    private void createDatabase() {

        Database.createTables();

        try (
                Connection connection = Database.connect();
                Statement statement = connection.createStatement()
        ) {

            ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM trains"
            );

            if (resultSet.next() && resultSet.getInt(1) == 0) {

                insertTrain(
                        connection,
                        "12601",
                        "Chennai Express",
                        "Chennai",
                        "Bangalore",
                        "20-09-2026",
                        50
                );

                insertTrain(
                        connection,
                        "12602",
                        "Coimbatore Express",
                        "Coimbatore",
                        "Chennai",
                        "21-09-2026",
                        45
                );

                insertTrain(
                        connection,
                        "12603",
                        "Kovai Superfast",
                        "Coimbatore",
                        "Bangalore",
                        "22-09-2026",
                        40
                );

                insertTrain(
                        connection,
                        "12604",
                        "Tamil Nadu Express",
                        "Chennai",
                        "Delhi",
                        "23-09-2026",
                        60
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void insertTrain(
            Connection connection,
            String number,
            String name,
            String source,
            String destination,
            String date,
            int seats
    ) throws SQLException {

        String sql = """
                INSERT INTO trains
                (train_number, train_name, source, destination,
                 travel_date, available_seats)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, number);
            statement.setString(2, name);
            statement.setString(3, source);
            statement.setString(4, destination);
            statement.setString(5, date);
            statement.setInt(6, seats);

            statement.executeUpdate();
        }
    }

    private void loadTrains() {

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM trains";

        try (
                Connection connection = Database.connect();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                tableModel.addRow(new Object[]{
                        resultSet.getString("train_number"),
                        resultSet.getString("train_name"),
                        resultSet.getString("source"),
                        resultSet.getString("destination"),
                        resultSet.getString("travel_date"),
                        resultSet.getInt("available_seats")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load trains: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadTrainDetails() {

        String trainNumber =
                trainNumberField.getText().trim();

        if (trainNumber.isEmpty()) {
            return;
        }

        if (!trainNumber.matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric.",
                    "Invalid Train Number",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "SELECT * FROM trains WHERE train_number = ?";

        try (
                Connection connection = Database.connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, trainNumber);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                trainNameField.setText(
                        resultSet.getString("train_name")
                );

                sourceField.setText(
                        resultSet.getString("source")
                );

                destinationField.setText(
                        resultSet.getString("destination")
                );

                travelDateField.setText(
                        resultSet.getString("travel_date")
                );

            } else {

                trainNameField.setText("");
                sourceField.setText("");
                destinationField.setText("");
                travelDateField.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Train not found.",
                        "Invalid Train",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean isValidDate(String date) {

        SimpleDateFormat format =
                new SimpleDateFormat("dd-MM-yyyy");

        format.setLenient(false);

        try {

            format.parse(date);
            return true;

        } catch (ParseException e) {

            return false;
        }
    }

    private void bookTicket() {

        String passengerName =
                passengerField.getText().trim();

        String trainNumber =
                trainNumberField.getText().trim();

        String trainName =
                trainNameField.getText().trim();

        String classType =
                classTypeComboBox.getSelectedItem().toString();

        String travelDate =
                travelDateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        // Required field validation
        if (
                passengerName.isEmpty()
                        || trainNumber.isEmpty()
                        || trainName.isEmpty()
                        || travelDate.isEmpty()
                        || source.isEmpty()
                        || destination.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Numeric train number validation
        if (!trainNumber.matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Date validation
        if (!isValidDate(travelDate)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format.\nUse DD-MM-YYYY.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String selectSql =
                "SELECT * FROM trains WHERE train_number = ?";

        try (
                Connection connection = Database.connect();
                PreparedStatement select =
                        connection.prepareStatement(selectSql)
        ) {

            select.setString(1, trainNumber);

            ResultSet resultSet =
                    select.executeQuery();

            if (!resultSet.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Train not found.",
                        "Booking Failed",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            int seats =
                    resultSet.getInt("available_seats");

            if (seats <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No seats available.",
                        "Booking Failed",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String pnr =
                    "PNR" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

            String insertSql = """
                    INSERT INTO reservations
                    (pnr, passenger_name, train_number,
                     train_name, class_type, source,
                     destination, travel_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (
                    PreparedStatement insert =
                            connection.prepareStatement(insertSql)
            ) {

                insert.setString(1, pnr);
                insert.setString(2, passengerName);
                insert.setString(3, trainNumber);
                insert.setString(4, trainName);
                insert.setString(5, classType);
                insert.setString(6, source);
                insert.setString(7, destination);
                insert.setString(8, travelDate);

                insert.executeUpdate();
            }

            String updateSql =
                    "UPDATE trains SET available_seats = ? " +
                    "WHERE train_number = ?";

            try (
                    PreparedStatement update =
                            connection.prepareStatement(updateSql)
            ) {

                update.setInt(1, seats - 1);
                update.setString(2, trainNumber);

                update.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Booking Successful!\n\n"
                            + "PNR: " + pnr + "\n"
                            + "Passenger: " + passengerName + "\n"
                            + "Train: " + trainName + "\n"
                            + "Class: " + classType + "\n"
                            + "Date: " + travelDate + "\n"
                            + "Route: " + source
                            + " -> " + destination,
                    "Booking Confirmation",
                    JOptionPane.INFORMATION_MESSAGE
            );

            passengerField.setText("");
            trainNumberField.setText("");
            trainNameField.setText("");
            travelDateField.setText("");
            sourceField.setText("");
            destinationField.setText("");
            classTypeComboBox.setSelectedIndex(0);

            loadTrains();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking failed: " + e.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void viewReservation() {

        String pnr =
                pnrField.getText().trim();

        if (pnr.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "SELECT * FROM reservations WHERE pnr = ?";

        try (
                Connection connection = Database.connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, pnr);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String details =
                        "PNR: "
                                + resultSet.getString("pnr")
                                + "\nPassenger: "
                                + resultSet.getString("passenger_name")
                                + "\nTrain: "
                                + resultSet.getString("train_number")
                                + "\nTrain Name: "
                                + resultSet.getString("train_name")
                                + "\nClass: "
                                + resultSet.getString("class_type")
                                + "\nDate: "
                                + resultSet.getString("travel_date")
                                + "\nSource: "
                                + resultSet.getString("source")
                                + "\nDestination: "
                                + resultSet.getString("destination");

                JOptionPane.showMessageDialog(
                        this,
                        details,
                        "Reservation Details",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Reservation not found.",
                        "Not Found",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cancelTicket() {

        String pnr =
                pnrField.getText().trim();

        if (pnr.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String selectSql =
                "SELECT * FROM reservations WHERE pnr = ?";

        try (
                Connection connection = Database.connect();
                PreparedStatement select =
                        connection.prepareStatement(selectSql)
        ) {

            select.setString(1, pnr);

            ResultSet resultSet =
                    select.executeQuery();

            if (!resultSet.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Reservation not found.",
                        "Not Found",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String trainNumber =
                    resultSet.getString("train_number");

            String details =
                    "PNR: "
                            + resultSet.getString("pnr")
                            + "\nPassenger: "
                            + resultSet.getString("passenger_name")
                            + "\nTrain: "
                            + resultSet.getString("train_name")
                            + "\nClass: "
                            + resultSet.getString("class_type")
                            + "\nDate: "
                            + resultSet.getString("travel_date")
                            + "\nRoute: "
                            + resultSet.getString("source")
                            + " -> "
                            + resultSet.getString("destination");

            JOptionPane.showMessageDialog(
                    this,
                    details,
                    "Reservation Details",
                    JOptionPane.INFORMATION_MESSAGE
            );

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to cancel this ticket?",
                            "Confirm Cancellation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            String deleteSql =
                    "DELETE FROM reservations WHERE pnr = ?";

            try (
                    PreparedStatement delete =
                            connection.prepareStatement(deleteSql)
            ) {

                delete.setString(1, pnr);
                delete.executeUpdate();
            }

            String updateSql =
                    "UPDATE trains SET available_seats = " +
                    "available_seats + 1 " +
                    "WHERE train_number = ?";

            try (
                    PreparedStatement update =
                            connection.prepareStatement(updateSql)
            ) {

                update.setString(1, trainNumber);
                update.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket cancelled successfully.",
                    "Cancellation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            pnrField.setText("");

            loadTrains();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed: " + e.getMessage(),
                    "Cancellation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}