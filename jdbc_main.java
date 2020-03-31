import java.sql.*;
import java.util.Scanner;

public class jdbc_main {

    // The instance variables for the class
    private Connection connection;
    private Statement statement;

    // The constructor for the class
    public jdbc_main() {
        connection = null;
        statement = null;
    }

    // The main program", that tests the methods
    public static void main(String[] args) throws SQLException {
        String Username = "gbglenn"; // Change to your own username
        String mysqlPassword = "EiGaix8o"; // Change to your own mysql Password

        jdbc_main test = new jdbc_main();
        test.connect(Username, mysqlPassword);
        test.initDatabase(Username, mysqlPassword, Username);

        // Scanner
        Scanner sc = new Scanner(System.in);

        scan: while (true) {
            String input = sc.nextLine();

            //Show GUI
            System.out.println("Input a number corrosponding to the functionality to be used.");
            System.out.println("1) Find all existing agents in a given city");
            System.out.println("2) Purchase an available policy from a particular agent");
            System.out.println("3) List all policies sold by a particular agent");
            System.out.println("4) Cancel a policy");
            System.out.println("5) Add a new agent for a city");
            System.out.println("6) Quit");

            switch(input){
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "6":
                    break scan;
                default:
                    System.out.println("You put an invalid character, returning to menu.");
            }

        }

        test.disConnect();
    }

    // Connect to the database
    public void connect(String Username, String mysqlPassword) throws SQLException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/" + Username + "?" + "user=" + Username + "&password=" + mysqlPassword);
            // connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username
            // +
            // "?user=" + Username + "&password=" + mysqlPassword);
        } catch (Exception e) {
            throw e;
        }
    }

    // Disconnect from the database
    public void disConnect() throws SQLException {
        connection.close();
        statement.close();
    }

    // Execute an SQL query passed in as a String parameter
    // and print the resulting relation
    public void query(String q) {
        try {
            ResultSet resultSet = statement.executeQuery(q);
            System.out.println("\n---------------------------------");
            System.out.println("Query: \n" + q + "\n\nResult: ");
            print(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Print the results of a query with attribute names on the first line
    // Followed by the tuples, one per line
    public void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        printHeader(metaData, numColumns);
        printRecords(resultSet, numColumns);
    }

    // Print the attribute names
    public void printHeader(ResultSetMetaData metaData, int numColumns) throws SQLException {
        for (int i = 1; i <= numColumns; i++) {
            if (i > 1)
                System.out.print(",  ");
            System.out.print(metaData.getColumnName(i));
        }
        System.out.println();
    }

    // Print the attribute values for all tuples in the result
    public void printRecords(ResultSet resultSet, int numColumns) throws SQLException {
        String columnValue;
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (i > 1)
                    System.out.print(",  ");
                columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    // Insert into any table, any values from data passed in as String parameters
    public void insert(String table, String values) {
        String query = "INSERT into " + table + " values (" + values + ")";
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove all records and fill them with values for testing
    // Assumes that the tables are already created
    public void initDatabase(String Username, String Password, String SchemaName) throws SQLException {
        statement = connection.createStatement();
    }
}