package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/7/2016.
 */
public abstract class DatabaseOperations  {
    static Context context;
    static LocalDatabaseOperations local;
    static RemoteDatabaseOperations remote;

    public static void DatabaseOperationsInit(Context context) {
        DatabaseOperations.context = context;
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations();
    }

    // logs user out of databases
    public static void logout(String username) {
        local.logout(username);
        remote.logout(username);
    }

    // logs user into databases. returns false if password/username do not match
    public static boolean login(String username, String password)
    {

        if (!local.login(username, password)) {
             return false;
        }

        if (!remote.login(username, password)) {
            System.out.println("Play with friends not available.  Please check your network connection and credentials.");
        }

        return true;
    }

    // returns username of local user logged in from previous session. returns null if no users logged in.
    public static String getLocalLoggedInUser()
    {
        return local.getLocalLoggedInUser();
    }

    // set high score for user
    public static void setHighScore(String username, int score)
    {
        local.setHighScore(username, score);
        remote.setHighScore(username, score);
    }

    // return high score for user. returns integer high score
    public static long getHighScore(String username)
    {
        if (remote.getLoginStatus(username))
            return remote.getHighScore(username);
        else
            return local.getHighScore(username);

    }

    // add username to databases. returns false if username is already taken
    public static boolean addUser(String username, String password) {
        boolean ret;
        // Ensure username only contains valid characters
        if (username.contains("\\"))
            System.out.println("usernames may not contain \"\\\"");
        if (username.contains("'"))
            System.out.println("usernames may not contain \"'\"");

        ret = local.addUser(username, password);

        if (!remote.addUser(username, password)) {
            System.out.println("user '"+ username +"' could not be added to the remote database");
        }

        return ret;
    }

    // resets password for user. returns false if password does not match
    public static boolean setPassword(String username, String oldPassword, String newPassword)
    {
        boolean ret = true;

        if (local.setPassword(username, oldPassword, newPassword))
            ret = remote.setPassword(username, oldPassword, newPassword);
        else
            return false;

        if (!ret)
            local.setPassword(username, newPassword, oldPassword);

        return false;
    }

    // deletes user from system. returns false if password does not match
    public static boolean deleteUser(String username, String password) {
        boolean ret = true;

        if (local.deleteUser(username, password)) {
            if (!remote.deleteUser(username, password)) {
                local.addUser(username, password);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // returns an ArrayList including all friends for a particular user
    public static ArrayList<String> getFriendsList(String username) {
        return local.getFriendsList(username);
    }

    // adds a new friend to a user's friends list
    public static void addNewFriend(String usernameOwner, String usernameFriend) {
        local.addNewFriend(usernameOwner, usernameFriend);
    }

    // deletes a friend from a user's friends list
    public static void deleteFriend(String usernameOwner, String usernameFriend) {
        local.deleteFriend(usernameOwner, usernameFriend);
    }
}
