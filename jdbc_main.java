//Database management systems
//Homework 4
// Gavin Glenn and John Ostermueller

import java.sql.*;
import java.util.Scanner;

public class jdbc_main {
    // The instance variables for the class
    private static Connection connection;
    private static Statement statement;
    private static int clientID_MAX;
    private static int purchaseID_MAX;
    private static Scanner sc = new Scanner(System.in);

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

        scan: while (true) {
            // Show GUI
            System.out.println("Input a number corrosponding to the functionality to be used.");
            System.out.println("1) Find all existing agents in a given city");
            System.out.println("2) Purchase an available policy from a particular agent");
            System.out.println("3) List all policies sold by a particular agent");
            System.out.println("4) Cancel a policy");
            System.out.println("5) Add a new agent for a city");
            System.out.println("6) Quit");

            // Get input
            String input = input("Input a number: ");

            // Switch
            switch (input) {
                case "1":
                    String city1 = input("Please input a city to search for: ");
                    getAgentClient(test, city1);
                    break;
                case "2":
                    // Info for client
                    System.out.println("Enter the following variables:");
                    String name = input("Name: ");
                    String city2 = input("City: ");
                    String zip = input("Zip: ");

                    // Get Client ID //Check to see if already created
                    // -1 if not in table, else in table
                    int clientID = getClientID(test, name, city2);
                    // If not in table create a new one
                    if (clientID == -1) {
                        // Inserts into Clients
                        insert("CLIENTS (C_ID, C_NAME, C_CITY, C_ZIP)",
                                ++clientID_MAX + ",'" + name + "','" + city2 + "'," + zip);
                        // update ClientID
                        clientID = getClientID(test, name, city2);
                    }

                    // TESTING
                    // SHOW CLIENT TABLE AFTER CHECK
                    test.query("SELECT * FROM CLIENTS");
                    // END TESTING

                    // Check Type in table
                    String policy_type = input("Enter a type of policy: ");
                    // Make Query to check for type
                    String queryCheck = "SELECT * FROM POLICY WHERE TYPE = '" + policy_type + "'";
                    ResultSet check = statement.executeQuery(queryCheck);
                    boolean isValid = check.first();

                    if (isValid) {
                        // display agents of city and list policies
                        showAgentsPolicies(test, city2, policy_type);
                    } else {
                        System.out.println("TYPE of policy not found. Returning");
                        break;
                    }

                    // Purchase
                    System.out.println("Enter the following variables for your purchase:");
                    String policyID = input("Policy_ID: ");
                    String amount = input("Amount: ");
                    String agentID = input("Agent_ID: ");

                    // Insert new policy that was sold and increment purchaseID
                    insert("POLICIES_SOLD (PURCHASE_ID, AGENT_ID, CLIENT_ID, POLICY_ID, DATE_PURCHASED, AMOUNT)",
                            ++purchaseID_MAX + "," + agentID + "," + clientID + "," + policyID + ",CURDATE(),"
                                    + amount);

                    // TESTING
                    // SHOW CLIENT TABLE AFTER CHECK
                    test.query("SELECT * FROM POLICIES_SOLD");
                    // END TESTING

                    break;
                case "3": // list all policies sold by a particular agent
                    System.out.println("Search for an Agent:");
                    String aName = input("Enter Agents name: ");
                    String city3 = input("Enter Agent's City: ");

                    String query3 = "SELECT A_NAME FROM AGENTS WHERE A_NAME = '" + aName + "' AND A_CITY='" + city3
                            + "'";
                    ResultSet agentCheck = statement.executeQuery(query3);
                    // Go to first row of table
                    boolean validAgent = agentCheck.first();

                    if (!validAgent) {
                        System.out.println("Agent or City is invalid. Returning to menu.");
                        break;
                    }

                    showAgentInfo(test, aName);

                    break;
                case "4":
                    // showPoliciesSold(test);
                    String purchase_id = inputLine("Enter the purchase ID of the policy you wish to cancel:");
                    cancelPolicy(purchase_id);
                    break;
                case "5": // Prompt the user for the (A_ID, A_NAME, A_CITY, A_ZIP) of the new agent.
                    System.out.println("Please enter the necessary information for the new agent:");
                    String a_id = input("Agent ID: ");
                    String a_name = input("Agent's name: ");
                    String a_city = input("Agent's city: ");
                    String a_zip = input("Agent's zip: ");

                    addAgent(test, a_id, a_name, a_city, a_zip);
                    break;
                case "6":
                    break scan;
                default:
                    System.out.println("You entered an invalid character, returning to menu.");
            }

        }
        sc.close();
        test.disConnect();
    }

    public static String input(String s) {
        System.out.print(s);
        String var = sc.nextLine();
        return var.toUpperCase();
    }

    public static String inputLine(String s) {
        System.out.println(s);
        String var = sc.nextLine();
        return var.toUpperCase();
    }

    // Case 1
    // Find all Agents and Clients in City
    // Variables: city
    public static void getAgentClient(jdbc_main jd, String city) {
        String agents = "SELECT * FROM AGENTS WHERE A_CITY = " + "\'" + city + "\'";
        String clients = "SELECT * FROM CLIENTS WHERE C_CITY = " + "\'" + city + "\'";
        System.out.println("-------------AGENTS-------------");
        jd.query(agents);
        System.out.println("-------------CLIENTS------------");
        jd.query(clients);
    }

    // Case 2
    // Purchase an available policy from a particular agent
    // Variables: city, type,
    // name, city
    public static void showAgentsPolicies(jdbc_main jd, String city, String type) {
        // Agents
        System.out.println("--------------AGENTS-------------");
        String agents = "SELECT * FROM AGENTS WHERE A_CITY = '" + city + "'";
        jd.query(agents);
        // Policies

        System.out.println("-------------POLICIES-------------");
        String policies = "SELECT * FROM POLICY WHERE TYPE = '" + type + "'";
        jd.query(policies);
    }

    public static int getClientID(jdbc_main jd, String name, String city) throws SQLException {
        // Will return -1 if not found
        String find = "SELECT * FROM CLIENTS WHERE C_CITY = '" + city + "' AND C_NAME = '" + name + "'";
        System.out.println("FINDING CLIENT WITH SQL: " + find);

        ResultSet check = statement.executeQuery(find);
        // Go to first row of table
        boolean isValid = check.first();

        // Find the ClientID, if in the table use it
        // if not in the table make a new one using the global variable
        if (isValid) {
            return check.getInt("C_ID");
        } else {
            return -1;
        }
    }

    public static int getMaxClientID() throws SQLException {
        int max = 0;
        ResultSet rs = statement.executeQuery("SELECT C_ID FROM CLIENTS");
        while (rs.next()) {
            int current = rs.getInt("C_ID");
            if (current > max)
                max = current;
        }
        return max;
    }

    public static int getMaxPurchaseID() throws SQLException {
        int max = 0;
        ResultSet rs = statement.executeQuery("SELECT PURCHASE_ID FROM POLICIES_SOLD");
        while (rs.next()) {
            int current = rs.getInt("PURCHASE_ID");
            if (current > max)
                max = current;
        }
        return max;
    }

    // Case 3
    // List all policies sold by a particular agent
    // Variables
    public static void showAgentInfo(jdbc_main jd, String aName) {
        System.out.println("-----------AGENTS POLICIES-----------");
        String showPoliciesforAgent = "SELECT * " + "FROM AGENTS "
                + "INNER JOIN POLICIES_SOLD ON AGENTS.A_ID = POLICIES_SOLD.AGENT_ID " + "WHERE AGENTS.A_NAME = '"
                + aName + "'";
        jd.query(showPoliciesforAgent);

        System.out.println("-----------POLICY DATA-----------");
        String showAllInfoAgent = "SELECT POLICY.NAME, POLICY.TYPE, POLICY.COMMISSION_PERCENTAGE " + "FROM AGENTS "
                + "INNER JOIN POLICIES_SOLD ON AGENTS.A_ID = POLICIES_SOLD.AGENT_ID "
                + "INNER JOIN POLICY ON POLICIES_SOLD.POLICY_ID = POLICY.POLICY_ID " + "WHERE AGENTS.A_NAME = '" + aName
                + "'";
        jd.query(showAllInfoAgent);
    }

    // Case 4
    // Cancel a policy
    // Variables: none,
    // purchase_id
    public static void showPoliciesSold(jdbc_main jd) {
        System.out.println("-----------POLICIES SOLD-----------");
        jd.query("SELECT * FROM POLICIES_SOLD");
    }

    public static void cancelPolicy(String purchase_id) {
        String del = "DELETE FROM POLICIES_SOLD WHERE PURCHASE_ID=" + purchase_id;
        try {
            statement.executeUpdate(del);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Policy " + purchase_id + " has been cancelled.");
    }

    // Case 5
    // Add a new agent with given info, then show other agents in that city
    // Variables: a_id, a_name, a_city, a_zip
    public static void addAgent(jdbc_main jd, String a_id, String a_name, String a_city, String a_zip) {
        insert("AGENTS", a_id + ", '" + a_name + "', '" + a_city + "', " + a_zip);
        jd.query("SELECT * FROM AGENTS WHERE A_CITY='" + a_city + "'");
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
        System.out.println("");
    }

    // Insert into any table, any values from data passed in as String parameters
    public static void insert(String table, String values) {
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
        clientID_MAX = getMaxClientID();
        purchaseID_MAX = getMaxPurchaseID();
    }
}