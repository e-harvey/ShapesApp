/**
 * Remote storage method implementations.
 * TODO: Test the rest of the implemented methods.  Update all methods involving password
 * to use the same hash function that local storage does.
 */

package storage.shapes;

import java.sql.*;
import java.util.concurrent.TimeUnit;


class RemoteDatabaseOperations implements RemoteDbOperations, SharedDbOperations  {
    final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String sqlCmd = null;
    private boolean status = false;
    private int max_retries = 5;

    private enum RemoteConnectionStatus {
        Success, Failure
    }

    /* Begin constructors */
    RemoteDatabaseOperations() {
        persistentConnect();
    }
    /* End constructors */

    /* Begin Private methods */

    /**
     * This method attempts to connect to the remote database at DB_URL using the
     * dbuser and password and credentials.  If the connection is esablished successfully,
     * then a statement is instantiated; otheriwse an error code is returned.
     *
     * @return Success: upon sucessfully connecting to the database.
     *         Failure: upon fauling to connect to the database.
     *
     *
     */
    private RemoteConnectionStatus connect()
    {
        final String DB_URL = "jdbc:mariadb://www.evanharvey.net:3306/shapes";
        final String dbuser = "";
        final String password = "";
        RemoteConnectionStatus remoteConnectionStatus = RemoteConnectionStatus.Success;

        try {
            // Point to the database driver
            Class.forName(JDBC_DRIVER);

            // Attempt to establish the connection
            connection = DriverManager.getConnection(DB_URL, dbuser, password);

            // Create the statement object for querying / updating the database
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            remoteConnectionStatus = RemoteConnectionStatus.Failure;
        } catch (SQLException e) {
            e.printStackTrace();
            remoteConnectionStatus = RemoteConnectionStatus.Failure;
        }

        return remoteConnectionStatus;
    }

    /**
     * This method attempts to connect to the database max_retires times and
     * then prints the status of the connection.
     */
    private void persistentConnect()
    {
        int count = 0;
        RemoteConnectionStatus remoteConnectionStatus = RemoteConnectionStatus.Failure;

        while (remoteConnectionStatus != RemoteConnectionStatus.Success && ++count < max_retries) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            remoteConnectionStatus = connect();
        }
        printRemoteConnectionStatus(remoteConnectionStatus);
    }

    /**
     * This method prints out the connection status information based on the
     * value of remoteConnectionStatus.
     *
     * @param remoteConnectionStatus the status of the remote connection.
     * todo: Print messages to the user's screen.
     */
    private void printRemoteConnectionStatus(RemoteConnectionStatus remoteConnectionStatus)
    {
        String userStatus = null;

        switch(remoteConnectionStatus) {
            case Success:
                userStatus = "You have successfully connected to the database!";
                break;

            default:
                userStatus = "Please check your local network connection.";
        }
        System.out.println(userStatus);
    }

    /**
     * Thie method returns true or false, true if the status of the user is online
     * and false other wise
     *
     * @param username the user's username
     * @return true if online; otherwise false
     */
    private boolean getStatus(String username)
    {
        sqlCmd = "select status from user where username = '" + username + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);

            if (resultSet.next()) {
                return resultSet.getBoolean("status");
            } else {
                    System.out.println("error: RemoteDatabaseOperations.getStatus: " +
                            "username '" + username + "' not found in remote database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getErrorCode() == -1) {
                    System.out.println("error: RemoteDatabaseOperations.getStatus: " +
                            "attempting to reconnect to remote database.");
                    connect();
            }
        }
        return false;
    }

    /**
     * Thie method checks the provided password against the password hash
     * stored in the database.
     *
     * @param username the user's username
     * @param password the password associated with the given username
     * @return true if the password matches the stored password; otherwise false
     */
    private boolean getHashMatch(String username, String password)
    {
        boolean hashMatch = false;
        sqlCmd = "select passwdhash from user where username = '" + username + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);

            if (resultSet.next()) {
                String hash = resultSet.getString("passwdhash");
                hashMatch = hash.equals(password);


            } else {
                System.out.println("error: RemoteDatabaseOperations.login could not find username '" +
                        username + "in the remote database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();

            System.out.println("error: RemoteDatabaseOperations.login: " +
                    "attempting to reconnect to remote database.");
            connect();
        }

        return hashMatch;
    }
    /* End private methods */

    /* Begin RemoteDbOperation methods */
    public boolean getLoginStatus(String username)
    {
        try {
            // Check if connection is valid and open, try for max_retries seconds
            if (connection.isValid(max_retries)) {
                return getStatus(username);
            } else {
                System.out.println("error: RemoteDatabaseOperations.getLoginStatus: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();

            // Connection failed, attempt to reconnect.
            System.out.println("error: RemoteDatabaseOperations.getLoginStatus: " +
                    "attempting to reconnect to remote database.");
            connect();

        }
        return false;
    }

    public long getBlockSeed(String username)
    {
        long seed = -1;

        if (getStatus(username)) {
            sqlCmd = "select seed from blockseed where username = '" + username + "'";

            try {
                resultSet = statement.executeQuery(sqlCmd);
                if (resultSet.next()) {
                    seed = resultSet.getLong("seed");
                }
            } catch (SQLException e) {
                e.printStackTrace();

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.getBlockSeed: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        } else {
            System.out.println("Hash match failed, please login first.");
        }
        return seed;
    }

    public void setBlockSeed(String username, long seed)
    {
        if (getStatus(username)) {
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

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.setBlockSeed: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        } else {
            System.out.println("Hash match failed, please login first.");
        }
    }
    /* End RemoteDbOperation methods */

    /* Begin SharedDatabaseOperations methods */
    public boolean addUser(String username, String password)
    {
        String status = "Successfully added user: '" + username + "'";
        boolean ret = false;

        sqlCmd = "select username, passwdhash from user where username = '" +
                username + "' AND passwdhash = '" + password + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);

            if (resultSet.next()) {
                status = "That username has already been taken.";
            } else { // add the user
                sqlCmd = "insert into user values ('" + username + "', '" +
                        password + "', '0', '0')";

                if (statement.executeUpdate(sqlCmd) == 0) {
                    status = "Failed to add user, but username doesn't exist!";
                } else
                    ret = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            // Connection failed, attempt to reconnect.
            System.out.println("error: RemoteDatabaseOperations.addUser: " +
                    "attempting to reconnect to remote database.");
            connect();
        }

        System.out.println(status);
        return ret;
    }

    public boolean deleteUser(String username, String password) {
        if (login(username, password)) {
            sqlCmd = "delete from user where username = '" + username + "'";

            try {
                // try updating the user table, if it fails print an error.
                if (statement.executeUpdate(sqlCmd) == 0) {
                    System.out.println("error: RemoteDatabaseOperations.deleteUser: username '"
                            + username + "' does not exist in the remote database.");
                    return false;
                }

                sqlCmd = "delete from blockseed where username = '" + username + "'";

                if (statement.executeUpdate(sqlCmd) == 0) {
                    System.out.println("error: RemoteDatabaseOperations.deleteUser: username '"
                            + username + "' does not exist in the remote database.");
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.deleteUser: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        }
        return false;
    }

    public void setHighScore(String username, long score) {
        if (getStatus(username)) {
            sqlCmd = "update user set highscore = '" + score + "' where username = '" +
                    username + "'";
            try {
                // try updating the user table, if it fails print an error.
                if (statement.executeUpdate(sqlCmd) == 0) {
                    System.out.println("error: RemoteDatabaseOperations.setHighScore: username '"
                            + username + "' does not exist in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.setHighScore: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        } else {
            System.out.println("Hash match failed, please login first.");
        }
    }

    public long getHighScore(String username)
    {
        if (getStatus(username)) {
            sqlCmd = "select highscore from user where username = '" + username + "'";
            try {
                resultSet = statement.executeQuery(sqlCmd);

                if (resultSet.next()) {
                    return resultSet.getLong("highscore");
                } else {
                    System.out.println("error: RemoteDatabaseOperations.getHighScore could not find username '" +
                            username + "in the remote database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.getHighScore: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        } else {
            System.out.println("Hash match failed, please login first.");
        }
        return -1;
    }

    public boolean login(String username, String password)
    {
        boolean hashMatch = false;

        if ((hashMatch = getHashMatch(username, password))) {
            sqlCmd = "update user set status = '1' where username = '" +
                    username + "'";

            try {
                if (statement.executeUpdate(sqlCmd) == 0) {
                    System.out.println("error: RemoteDatabaseOperations.login: username '"
                            + username + "' does not exist in the remote database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();

                System.out.println("error: RemoteDatabaseOperations.login: " +
                        "attempting to reconnect to remote database.");
                connect();

            }
        }
        return hashMatch;
    }

    public void logout(String username)
    {
        try {
            sqlCmd = "update user set status = '0' where username = '" +
                    username + "'";

            if (statement.executeUpdate(sqlCmd) == 0) {
                System.out.println("error: RemoteDatabaseOperations.logout: username '"
                        + username + "' does not exist in the remote database.");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean setPassword(String username, String oldPassword, String newPassword) {
        if (login(username, oldPassword)) {
            sqlCmd = "update user set passwdhash = '" + newPassword + "' where username = '" +
                    username + "'";
            try {
                // try updating the user table, if it fails print an error.
                if (statement.executeUpdate(sqlCmd) == 0) {
                    System.out.println("error: RemoteDatabaseOperations.setHighScore: username '"
                            + username + "' does not exist in the database.");
                    return false;
                }
                System.out.println("Password for user '" + username + "' updated.");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();

                // Connection failed, attempt to reconnect.
                System.out.println("error: RemoteDatabaseOperations.setPassword: " +
                        "attempting to reconnect to remote database.");
                connect();
            }
        }
        return false;
    }
    /* End SharedDatabaseOperations methods */
}
