import java.sql.*;
import java.util.Scanner;

public class jdbc_main {
    // The instance variables for the class
    private static Connection connection;
    private static Statement statement;

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
            // Show GUI
            System.out.println("Input a number corrosponding to the functionality to be used.");
            System.out.println("1) Find all existing agents in a given city");
            System.out.println("2) Purchase an available policy from a particular agent");
            System.out.println("3) List all policies sold by a particular agent");
            System.out.println("4) Cancel a policy");
            System.out.println("5) Add a new agent for a city");
            System.out.println("6) Quit");

            // Get input
            String input = sc.nextLine();

            // Switch
            switch (input) {
                case "1":
                    System.out.println("Please input a city to search for");
                    String city1 = sc.nextLine();
                    getAgentClient(test, city1);
                    break;
                case "2":
                    //Info for client
                    System.out.println("Enter the following variables:");
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("City: ");
                    String city2 = sc.nextLine();
                    System.out.print("Zip: ");
                    String zip = sc.nextLine();

                    //Get Client ID
                    String clientID = Integer.toString(getClientID(test, name, city2));
                    //Check to see if already created
                    if (clientID == "-1"){
                        //Inserts into Clients
                        insert("CLIENTS", name + "," + city2 + "," + zip);
                        //update ClientID
                        clientID = Integer.toString(getClientID(test, name, city2));
                    }

                    //Check Type in table
                    System.out.print("Enter a type of policy: ");
                    String policy_type = sc.nextLine();
                    String queryCheck = "SELECT * FROM POLICY WHERE TYPE = '" + policy_type.toUpperCase() + "'";
                    ResultSet check = statement.executeQuery(queryCheck);
                    String is_type = check.getString("TYPE");
                    if(is_type == null){
                        System.out.print("Type not found, Exiting");
                        break;
                    }

                    //display agents of city and list policies
                    showAgentsPolicies(test, city2, policy_type);
                    //Purchase
                    System.out.println("Enter the following variables for your purchase:");
                    System.out.print("Policy_ID: ");
                    String policyID = sc.nextLine();
                    System.out.print("Amount: ");
                    String amount = sc.nextLine();
                    System.out.print("Agent_ID: ");
                    String agentID = sc.nextLine();
                    
                    //CLIENT ID NEEDS TO BE INSERTED
                    insert("POLICIES_SOLD", agentID + "," + clientID + "," + policyID + ",CURDATE()," + amount);

                    break;
                case "3": //list all policies sold by a particular agent 
                    break;
                case "4":
                    showPoliciesSold(test);
                    System.out.println("Enter the purchase ID of the policy you wish to cancel:");
                    String purchase_id = sc.nextLine();
                    cancelPolicy(purchase_id);
                    break;
                case "5": //Prompt the user for the (A_ID, A_NAME, A_CITY, A_ZIP) of the new agent.
                    System.out.println("Please enter the necessary information for the new agent:");
                    System.out.print("Agent ID: ");
                    String a_id = sc.nextLine();
                    System.out.print("Agent's name: ");
                    String a_name = sc.nextLine();
                    System.out.print("Agent's city: ");
                    String a_city = sc.nextLine();
                    System.out.print("Agent's zip: ");
                    String a_zip = sc.nextLine();
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

    // Case 1
    // Find all Agents and Clients in City
    // Variables: city
    public static void getAgentClient(jdbc_main jd, String city) {
        String agents = "SELECT * " + "FROM AGENTS " + "WHERE A_CITY = " + "\'" + city.toUpperCase() + "\'";
        String clients = "SELECT * " + "FROM CLIENTS " + "WHERE C_CITY = " + "\'" + city.toUpperCase() + "\'";
        System.out.println("-------------AGENTS-------------");
        jd.query(agents);
        System.out.println("-------------CLIENTS-------------");
        jd.query(clients);
    }

    // Case 2
    // Purchase an available policy from a particular agent
    // Variables: city, type, 
    //            name, city
    public static void showAgentsPolicies(jdbc_main jd, String city, String type){
        //Agents
        String agents = "SELECT * FROM AGENTS WHERE A_CITY = '" + city.toUpperCase() + "'";
        System.out.println("-------------AGENTS-------------");
        jd.query(agents);
        //Policies
        String policies = "SELECT * FROM POLICY WHERE TYPE = '" + type.toUpperCase() + "'";
        System.out.println("-------------POLICIES-------------");
        jd.query(policies);
    }
  
    public static int getClientID(jdbc_main jd, String name, String city) throws SQLException{
        //Will return -1 if not found
        String find = "SELECT * FROM CLIENTS WHERE C_CITY = '" + city.toUpperCase() + "' AND C_NAME = '" + name.toUpperCase() + "'";
        ResultSet check = statement.executeQuery(find);
        int clientID = check.getInt("C_ID");
        if (clientID == 0){
            return -1;
        } else {
            return clientID;
        }
    }

    public static int getMaxClientID(jdbc_main jd) {
        int max=0;
        ResultSet rs = test.executeQuery("SELECT C_ID FROM CLIENTS");
        while (rs.next()) {
            int current = rs.getInt("C_ID");
            if (current>max) max=current;
        }

        return max;
    }

    // Case 3
    // List all policies sold by a particular agent
    // Variables

    // Case 4
    // Cancel a policy
    // Variables: none, 
    //            purchase_id
    public static void showPoliciesSold(jdbc_main jd) {
        System.out.println("-----------POLICIES SOLD-----------");
        jd.query("SELECT * FROM POLICIES_SOLD");
    }
    public static void cancelPolicy(String purchase_id) {
        String del = "DELETE FROM POLICEIS_SOLD WHERE PURCHASE_ID="+purchase_id;
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
        String add = "INSERT INTO AGENTS VALUES ( " + a_id + ", '" + a_name + "', '" + a_city + "', " + a_zip + ")";
        try {
            statement.executeUpdate(add);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Agent " + a_id + " has been added.");
        jd.query("SELECT * FROM AGENTS WHERE A_CITY='" + a_city + "'");
    }


    // // Connect to the database
    // public void connect(String Username, String mysqlPassword) throws SQLException {
    //     try {
    //         connection = DriverManager.getConnection(
    //                 "jdbc:mysql://localhost/" + Username + "?" + "user=" + Username + "&password=" + mysqlPassword);
    //         // connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username
    //         // +
    //         // "?user=" + Username + "&password=" + mysqlPassword);
    //     } catch (Exception e) {
    //         throw e;
    //     }
    // }

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
    }
}