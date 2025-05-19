package proiect;

import javafx.scene.layout.AnchorPane;
import java.sql.*;

/**
 * Represents a utility class for handling database connections and operations.
 * This class facilitates connecting to a database, executing SQL queries and updates,
 * and managing connection lifecycles. It also provides utility methods for counting
 * or summing rows based on specific SQL queries.
 */
public class DatabaseConnection {
    private String url;
    private String user;
    private String password;
    private Connection connection;
    private AnchorPane menu_failedConnection;
    private ControllerLibrarian mainController;

    /**
     * Establishes a connection to the database using the provided credentials and URL.
     * This constructor initializes the connection and associates it with the main controller.
     * If the connection fails (e.g., invalid URL, user, or password), an error message is set in the main controller.
     *
     * @param url the database connection URL
     * @param user the username for authentication
     * @param password the password for authentication
     * @param mainController the main controller handling the application logic
     */
    public DatabaseConnection(String url, String user, String password, ControllerLibrarian mainController) {
        try{
            this.mainController = mainController;
            this.connection = DriverManager.getConnection(url,user,password);
            System.out.print("\nConexiune realizata!\n");
        } catch (SQLException e) {
            setFailed("Conexiune esuata.(verifica url/user/parola sa fie valide)");
        }
    }

    /**
     * Closes the database connection if it is currently open.
     * This method attempts to close the `connection` object associated
     * with the database. If the connection is successfully closed,
     * a confirmation message is printed to the console. In the event
     * of an SQLException, an error message indicating that the connection
     * could not be closed will be printed to the error stream.
     *
     * Note: Ensure this method is called to release the database resources
     * and avoid potential connection leaks.
     */
    public void close(){
        if(connection!=null)
            try {
                this.connection.close();
                System.out.print("\nConexiune incheiata!");
            }
            catch (SQLException e) {
                System.err.println("\nNu s-a putut inchide conexiunea.");
            }
    }

    /**
     * Executes a SQL query and returns the result set from the database.
     * This method prepares a SQL statement using the provided query,
     * executes it, and returns the resulting data as a `ResultSet` object.
     * If an SQLException occurs during query execution,
     * it logs the failure and throws a runtime exception.
     *
     * @param query the SQL query to be executed.
     *              Must be a valid SQL SELECT statement.
     * @return a `ResultSet` containing the data produced by the query.
     *         If the query fails, an exception is thrown.
     * @throws RuntimeException if an error occurs during SQL query execution.
     */
    public ResultSet executeQuery(String query) {
            try
            {
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet result = statement.executeQuery();
                System.out.print("\nSQL QUERRY:\n" + '\"' + query + "\"\n");
                return result;
            } catch (SQLException e) {
                setFailed("FAILED SQL QUERRY:\n" + '\"' + query);
                throw new RuntimeException(e);
            }
    }

    /**
     * Executes a SQL update statement on the database.
     * This method prepares the provided SQL update statement, executes it, and
     * logs the update operation. If an SQLException occurs during the execution,
     * the failure is logged, and a runtime exception is thrown.
     *
     * @param update the SQL update query to be executed.
     *               Must be a valid SQL UPDATE statement.
     * @throws RuntimeException if an error occurs during SQL update execution.
     */
    public void executeUpdate(String update){
            try
            {
                PreparedStatement statement = connection.prepareStatement(update);
                statement.executeUpdate();
                System.out.print("\nSQL UPDATE:\n" + '\"' + update + "\"\n");
            } catch (SQLException e) {
                setFailed("FAILED SQL UPDATE:\n" + '\"' + update);
                throw new RuntimeException(e);
            }
    }

    /**
     * Marks the database connection as failed and updates the user interface to display
     * a connection failure message.
     *
     * @param reason the reason for the connection failure to be displayed to the user.
     */
    void setFailed(String reason) {
        System.err.print('\n' + reason + "\n");
        mainController.setOnlyMenu(mainController.connectionFailed_menu);
    }

    /**
     * Retrieves the active database connection.
     *
     * @return the active {@link Connection} object representing the current connection to the database,
     *         or null if the connection is not initialized.
     */
    public Connection getConnection(){
        return this.connection;
    }

    /**
     * Counts the number of rows in the result set of the specified SQL query.
     * This method executes the provided SQL query and iterates through the
     * result set to count the total number of rows returned.
     *
     * @param query the SQL SELECT query to be executed.
     *              Must be a valid SQL SELECT statement.
     * @return the total number of rows in the result set.
     * @throws SQLException if an error occurs during the execution of the SQL query.
     */
    public int getQueryCount(String query) throws SQLException {
        ResultSet rs = executeQuery(query);
        int count = 0;
        while(rs.next())
            count++;
        return count;
    }

    /**
     * Calculates the sum of an integer column in the result set of the specified SQL query.
     * This method executes the provided SQL query, iterates through the rows of the result set,
     * and accumulates the values of the first column as integers into a total sum.
     *
     * @param query the SQL SELECT query to be executed.
     *              The query must return a result set with an integer column in the first position.
     * @return the sum of the integers in the first column of the result set.
     * @throws SQLException if an error occurs during the execution of the SQL query or while processing the result set.
     */
    public int getQuerrySum(String query) throws SQLException {
        ResultSet rs = executeQuery(query);
        int sum = 0;
        while(rs.next())
            sum += rs.getInt(1);
        return sum;
    }
}


