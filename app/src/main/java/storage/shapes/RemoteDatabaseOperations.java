package storage.shapes;

import java.sql.*;


/**
 * Created by Fritz on 4/7/2016.
 */
class RemoteDatabaseOperations extends SharedDatabaseOperations implements RemoteDbOperations {
    private boolean connected = false;

    RemoteDatabaseOperations() {
        this.connect();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // remote methods

    // return the login status of remote user
    public boolean getLoginStatus(String username) {
        return getLoginStatus(username);
    }

    public int getBlockSeed(String username) {
        return 0;
    }

    public void setBlockSeed(String username, int seed) { }

    public boolean connect() {
        final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
        final String DB_URL = "jdbc:mariadb://www.evanharvey.net:3306/shapes";
        final String dbuser = "";
        final String password = "";
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName(JDBC_DRIVER);

            connection = DriverManager.getConnection(DB_URL, dbuser, password);

            String test_query = "select * from user where username = 'testUser'";
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(test_query);
            if (resultSet.next()) {
                int hs = resultSet.getInt("highScore");
                String username = resultSet.getString("username");
                boolean status = resultSet.getBoolean("status");
                System.out.println(hs + " " + username + " " + status);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // begin extended methods. these should be complete already

    // add user to database. returns false if username is already taken
    public boolean addUser(String username, String password) {
        return addUser(username, password);
    }

    // log user out of database
    public void logout(String username) {
        logout(username);
    }

    // set high score for user in database
    public void setHighScore(String username, int score) {
        setHighScore(username, score);
    }

    // return high score of user from database
    public int getHighScore(String username) {
        return getHighScore(username);
    }

    // logs a user into database
    public boolean login(String username, String password) {
        return login(username, password);
    }


    // reset the password.  will return false if old password does not match stored password
    public boolean setPassword(String username, String oldPassword, String newPassword) {
        return setPassword(username, oldPassword, newPassword);
    }

    // deletes user from local database.  returns false if password does not match.
    public boolean deleteUser(String username, String password) {
        return deleteUser(username, password);
    }

}
