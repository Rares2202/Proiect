package proiect;

import javafx.scene.layout.AnchorPane;
import java.sql.*;

public class DatabaseConnection {
    private String url;
    private String user;
    private String password;
    private Connection connection;
    private AnchorPane menu_failedConnection;
    private ControllerLibrarian mainController;

    public DatabaseConnection(String url, String user, String password, ControllerLibrarian mainController) {
        try{
            this.mainController = mainController;
            this.connection = DriverManager.getConnection(url,user,password);
            System.out.print("\nConexiune realizata!\n");
        } catch (SQLException e) {
            setFailed("Conexiune esuata.(verifica url/user/parola sa fie valide)");
        }
    }
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
    void setFailed(String reason) {
        System.err.print('\n' + reason + "\n");
        mainController.setOnlyMenu(mainController.connectionFailed_menu);
    }
    public Connection getConnection(){return this.connection;}
    public int getQueryCount(String query) throws SQLException {
        ResultSet rs = executeQuery(query);
        int count = 0;
        while(rs.next())
            count++;
        return count;
    }
    public int getQuerrySum(String query) throws SQLException {
        ResultSet rs = executeQuery(query);
        int sum = 0;
        while(rs.next())
            sum += rs.getInt(1);
        return sum;
    }
}


