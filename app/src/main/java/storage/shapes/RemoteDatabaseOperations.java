package storage.shapes;

import java.sql.*;
import java.util.concurrent.TimeUnit;


class RemoteDatabaseOperations extends SharedDatabaseOperations implements RemoteDbOperations {
    final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private boolean hashMatch = false;
    private String sqlCmd = null;

    private enum RemoteConnectionStatus {Success,
        ClientNotConnected, ServerNotResponding, InvalidDBCredentials,
        JdbcDriverNotFound, Unknown}

    private int errCode = -1;
    private int max_retries = 5;

    /* Begin constructors */
    RemoteDatabaseOperations() {
        persistentConnect();
    }
    /* End constructors */

    /* Begin setters and getters */
    /* End setters and getters */

    /* Begin Private methods */

    /**
     * This method attempts to connect to the remote database at DB_URL using the
     * dbuser and password and credentials.  If the connection is esablished successfully,
     * then a statement is instantiated; otheriwse an error code is returned.
     *
     * @return Success:              upon sucessfully connecting to the database
     *         ClientNotConnected:   if the client has connectivity issues
     *         ServerNotResponding:  if the server has connectivity issues
     *         InvalidDBCredentials: if the applicaiton provides the incorrect credentials
     *         JdbcDriverNotFound:   if the database driver cannot be found on the local machine
     *         Unknown:              upon encountering and unknown connection error.
     *
     */
    private RemoteConnectionStatus connect()
    {
        final String DB_URL = "jdbc:mariadb://www.evanharvey.net:3306/shapes";
        final String dbuser = "";
        final String password = "";

        try {
            // Point to the databse driver
            Class.forName(JDBC_DRIVER);

            // Attempt to establish the connection
            connection = DriverManager.getConnection(DB_URL, dbuser, password);

            // Create the statement object for querying / updating the database
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            errCode = -1;

            return RemoteConnectionStatus.JdbcDriverNotFound;
        } catch (SQLException e) {
            e.printStackTrace();
            errCode = e.getErrorCode();
            System.out.println(errCode);
        } //finally {
            //printStatus();
        //}

        return RemoteConnectionStatus.Success;
    }

    /**
     * This method attempts to connect to the database max_retires times and
     * then prints the status of the connection.
     */
    private void persistentConnect()
    {
        int count = 0;
        RemoteConnectionStatus ret = RemoteConnectionStatus.Unknown;

        while (ret != RemoteConnectionStatus.Success && ++count < max_retries) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ret = connect();
        }
        printStatus(ret);
    }

    /**
     * This method prints out the connection status information based on the
     * value of remoteConnectionStatus.
     *
     * @param remoteConnectionStatus the status of the remote connection.
     * todo: Print messages to the user's screen.  Send admStatus to the admin email address.
     */
    private void printStatus(RemoteConnectionStatus remoteConnectionStatus)
    {
        String userStatus = null, admStatus = null;

        switch(remoteConnectionStatus) {
            case Success:
                userStatus = "You have successfully connected to the database!";
                admStatus = null;
                break;

            case ClientNotConnected:
                userStatus = "Please check your local network connection.";
                admStatus = null;
                break;

            case ServerNotResponding:
                userStatus = "The server is not responding, the administrator has been notified.";
                admStatus = "error: The server is not responding with error code: " + errCode;
                break;

            case InvalidDBCredentials:
                userStatus = "The server is not responding, the administrator has been notified.";
                admStatus = "error: The client is providing the incorrect credentials with error code: " + errCode;
                break;

            case JdbcDriverNotFound:
                userStatus = "The server is not responding, the administrator has been notified.";
                admStatus = "error: The client cannot find the jdbc driver with error code " + errCode;
                break;

            case Unknown:
                userStatus = "The server is not responding, the administrator has been notified.";
                admStatus = "error: Unknown error with error code " + errCode;
                break;
        }
        System.out.println(userStatus);
        System.out.println(admStatus);
    }
    /* End private methods */


    /* Begin RemoteDbOperation methods */
    public boolean getLoginStatus(String username)
    {
        try {
            // Check if connection is valid and open, try for max_retires seconds
            if (connection.isValid(max_retries)) {
                // If the provided password passed in login, then true.
                return hashMatch;
            } else {
                hashMatch = false;
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connect();
            return false;
        }
    }

    public long getBlockSeed(String username)
    {
        long seed = -1;
        sqlCmd = "select seed from blockseed where username = '" + username + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);
            if (resultSet.next()) {
                seed = resultSet.getLong("seed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seed;
    }

    public void setBlockSeed(String username, long seed)
    {
        sqlCmd = "update blockseed set seed = '" + seed + "' where username = '" +
                username + "'";
        try {
            // try updating the blockseed table, if it fails create the initial blockseed for the given user.
            if (statement.executeUpdate(sqlCmd) == 0) {
                System.out.println("Couldn't update user '" + username + "'" +
                        " in blockseed table, creating initial blockseed instead");

                sqlCmd = "insert into blockseed values ('" + username + "', '" + seed + "')";
                statement.executeUpdate(sqlCmd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connect();
        }
    }
    /* End RemoteDbOperation methods */

    /* Begin SharedDatabaseOperations methods */
    public boolean addUser(String username, String password)
    {
        String status = null;
        boolean ret = false;

        sqlCmd = "select username, passwdhash from user where username = '" +
                username + "' AND passwdhash = '" + password + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);

            if (resultSet.next()) {
                status = "That username has already been taken.";
            } else { // add the user
                sqlCmd = "insert into user values ('" + username + "', '" +
                        password + "', '0')";

                if (statement.executeUpdate(sqlCmd) == 0) {
                    status = "Failed to add user, but username doesn't exist!";
                } else
                    ret = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(status);
        return ret;
    }

    // log user out of database
    public void logout(String username)
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        hashMatch = false;
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
//        String hash = resultSet.getString("passwdhash");
  //      hashMatch = hashMatch.equals(password);

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
    /* End SharedDatabaseOperations metods */
}
