/**
 * Remote storage method implementations.
 */

package storage.shapes;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
    private SecureRandom secureRandom = null;
    private MessageDigest messageDigest = null;

    private enum RemoteConnectionStatus {
        Success, Failure
    }

    /* Begin constructors */
    RemoteDatabaseOperations() {
        persistentConnect();
        secureRandom = new SecureRandom();
        try {

            messageDigest = MessageDigest.getInstance("sha-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
            // Just return success if we are already connected.
            if (connection != null && connection.isValid(max_retries))
                return remoteConnectionStatus;

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
     * Set the hash for the given password.
     *
     * @param password the user's password
     * @return true if the hash was set; otherwise false.
     */
    private boolean setHash(String username, String password)
    {
        byte[] salt, passwd, saltedPasswd, hash;
        salt = passwd = saltedPasswd = hash = null;
        int saltInt;
        String hashStr;

        secureRandom.setSeed(System.currentTimeMillis());

        saltInt = Math.abs(secureRandom.nextInt());
        salt = ByteBuffer.allocate(4).putInt(saltInt).array();
        passwd = password.getBytes();
        saltedPasswd = new byte[salt.length + passwd.length];

        try {
            System.arraycopy(salt, 0, saltedPasswd, 0, salt.length);
            System.arraycopy(passwd, 0, saltedPasswd, salt.length, passwd.length);

            hash = messageDigest.digest(saltedPasswd);

            // Remove control character "'" from hash and backslash because reasons
            hashStr = new String(hash).replaceAll("(\\\\|')+", "0");

            sqlCmd = "update user set passwdhash = '" + hashStr +
                    "', salt = '" + saltInt + "' where username = '" +
                    username + "'";

            if (statement.executeUpdate(sqlCmd) == 0) {
                System.out.println("Couldn't update hash and salt for user '" + username + "'" +
                " in user table");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connect();
            return false;
        }
        return true;
    }

    /**
     * Determine whether the given password matches the stored hash.
     *
     * @param username the user's username
     * @param password the associated password
     * @return true if the password matches; otherwise false.
     */
    private boolean cmpHash(String username, String password)
    {
        String passwdhash = null;
        byte[] hash = null, salt, passwd;
        byte[] saltedPw;
        int saltInt;
        String hashStr;

        sqlCmd = "select salt, passwdhash from user where username = '" + username + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);

            if (resultSet.next()) {
                saltInt = resultSet.getInt("salt");
                passwdhash = resultSet.getString("passwdhash");
            } else {
                System.out.println("error: RemoteDatabaseOperations.cmpHash: " +
                        "no salt or password hash found for user '" + username + "'");
                return false;
            }

            salt = ByteBuffer.allocate(4).putInt(saltInt).array();
            passwd = password.getBytes();
            saltedPw = new byte[salt.length + passwd.length];
            System.arraycopy(salt, 0, saltedPw, 0, salt.length);
            System.arraycopy(passwd, 0, saltedPw, salt.length, passwd.length);

            hash = messageDigest.digest(saltedPw);

            // Remove control character "'" from hash and backslash because reasons
            hashStr = new String(hash).replaceAll("(\\\\|')+", "0");

            return passwdhash.equals(hashStr);
        } catch (SQLException e) {
            e.printStackTrace();
            connect();
        }
        return false;
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
                } else {
                    System.out.println("error: RemoteDatabaseOperations.getBlockSeed: no seed found" +
                            " for user '" + username + "'");
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
        byte[] salt, passwd, saltedPasswd, hash;
        int saltInt;
        String hashStr;
        salt = passwd = saltedPasswd = hash = null;

        secureRandom.setSeed(System.currentTimeMillis());
        saltInt = Math.abs(secureRandom.nextInt());

        salt = ByteBuffer.allocate(4).putInt(saltInt).array();
        passwd = password.getBytes();
        saltedPasswd = new byte[salt.length + passwd.length];

        System.arraycopy(salt, 0, saltedPasswd, 0, salt.length);
        System.arraycopy(passwd, 0, saltedPasswd, salt.length, passwd.length);
        hash = messageDigest.digest(saltedPasswd);

        // Remove control character "'" from hash and backslash because reasons
        hashStr = new String(hash).replaceAll("(\\\\|')+", "0");

        sqlCmd = "select username from user where username = '" + username + "'";

        try {
            resultSet = statement.executeQuery(sqlCmd);
            if (resultSet.next()) {
                status = "That username has already been taken.";
            } else { // add the user
                sqlCmd = "insert into user values ('" + username + "', '0', '0', +'" + saltInt + "', '" +
                        hashStr + "')";

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

        if ((hashMatch = cmpHash(username, password))) {
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
        } else {
            System.out.println("error: RemoteDatabaseOperations.login: hash match failed");
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
            connect();
        }
    }

    public boolean setPassword(String username, String oldPassword, String newPassword) {
        if (login(username, oldPassword)) {
            return setHash(username, newPassword);
        }
        return false;
    }
    /* End SharedDatabaseOperations methods */
}
