package hu.sztaki.lpds.statistics.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import hu.sztaki.lpds.information.local.PropertyLoader;
/**
 * Controls the database connection. Each DBBase object will create and return ONE connection until closed.
 * @author smoniz
 */
public class DBBase {

    /* private final String url;
    private final String pw;
    private final String user;

    public DBBase(String url, String user, String pw) {
    this.url = url;
    this.user = user;
    this.pw = pw;
    }*/
    private Connection con = null;

    public void close() {
        if (con != null) {
            try {
                con.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            con = null;
        }
    }


    /**
     * Returns a database connection
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
        if (con == null) {
            String dbdriver = PropertyLoader.getInstance().getProperty("guse.system.database.driver");
            Class.forName(dbdriver);
            //String dbURL = PropertyLoader.getInstance().getProperty("guse.system.database.url");
            //String dbUser = PropertyLoader.getInstance().getProperty("guse.system.database.user");
            //String dbPass = PropertyLoader.getInstance().getProperty("guse.system.database.password");


            //Properties props = PropertyManager.getProperties();

            String dbURL = PropertyLoader.getInstance().getProperty("guse.system.database.url");
            String dbUser = PropertyLoader.getInstance().getProperty("guse.system.database.user");
            String dbPass =PropertyLoader.getInstance().getProperty("guse.system.database.password"); 

            

            con = DriverManager.getConnection(dbURL, dbUser, dbPass);
        }
        else if (con.isClosed()) {
            con = null;
            return getConnection();
        }
        return con;

    }
}
