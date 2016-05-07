package storage.shapes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

public abstract class DatabaseOperations  {
    static Context context;
    static LocalDatabaseOperations local;
    static RemoteDatabaseOperations remote;
    private static boolean remoteLoginStatus = false;

    /**
     * This method is responsible for initializing the local and remote database
     * objects.  If those objects have already been created then there is no need
     * to created.
     * @param context the global information about the application's environment.
     */
    public static void DatabaseOperationsInit(Context context) {
        DatabaseOperations.context = context;

        if (local == null) {
            local = new LocalDatabaseOperations(context);
        }
        if (remote == null) {
            remote = new RemoteDatabaseOperations();
        }

        if (local.getLocalLoggedInUser() != null)
            remoteLoginStatus = remote.getLoginStatus(local.getLocalLoggedInUser());
    }

    /* Begin private helpler methods */
    /* Begin setters */
    private static void setRemoteLoginStatus(boolean status) {
        remoteLoginStatus = status;
    }

    public static boolean getRemoteLoginStatus() {
        return remoteLoginStatus;
    }
    /* End setters */
    /**
     gets login status of user in remote database
     */
    private static boolean isNetworkConnected()
    {
        boolean ret = false;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        ret = (networkInfo != null && networkInfo.isConnected());

        return ret;
    }
    /* End private helper methods */

    /* Begin shared database operations */
    /**
     * Log the user out of the given database.
     *
     * @param username the user's username
     */
    public static void logout(String username) {
        local.logout(username);

        if (isNetworkConnected())
            remote.logout(username);
    }

    /**
     * Log the user into the given database.
     *
     * @param username the user's username
     * @param password the given user's password
     * @return true if the user has been logged in; otherwise false.
     */
    public static boolean login(String username, String password)
    {
        if (isNetworkConnected()) {
            // Check to see if user exists in remote db but not local db
            if (remote.login(username, password) && !local.login(username, password)) {
                // Create user in local db
                local.addUser(username, password);

                // Now add token to local db
                remote.login(username, password);

                // Set highscore of user in local db
                local.setHighScore(username, remote.getHighScore(username));

                ArrayList<String> friends = remote.getFriendsList(username);

                if (friends != null) {
                    for (int i = 0; i < friends.size(); i++) {
                        //System.out.println("adding: " + friends.get(i));
                        local.addNewFriend(username, friends.get(i));
                    }
                }
                setRemoteLoginStatus(true);
            } else if (!remote.login(username, password)) {
                //System.out.println("Play with friends is not available." +
                        //"  Please check your credentials.");
                setRemoteLoginStatus(false);
            } else {
                setRemoteLoginStatus(true);
                //System.out.println("Successfully contacted the remote database and logged user '" + username + "' in.");
            }
        } else {
            setRemoteLoginStatus(false);
            //System.out.println("Failed to contact remote database.");
        }

        if (!local.login(username, password)) {
            return false;
        }

        return true;
    }

    /**
     * Set the highScore for the given user.
     *
     * @param username the user's username.
     * @param score    the new highscore.
     */
    public static void setHighScore(String username, long score)
    {
        local.setHighScore(username, score);

        if (isNetworkConnected())
            remote.setHighScore(username, score);
    }

    /**
     * Get the highscore for the given user.
     *
     * @param username the user's username.
     * @return the highscore, or -1 on error.
     */
    public static long getHighScore(String username)
    {
        long ret = local.getHighScore(username);

        if (ret == -1 && isNetworkConnected())
            return remote.getHighScore(username);

        return ret;
    }

    /**
     * Add the given user to the given database.
     *
     * @param username the desired username
     * @param password the desired password
     * @return true if the user is added to the database; otherwise false
     */
    public static boolean addUser(String username, String password) {
        boolean ret = false;

        // Ensure username and password are non-empty
        if (username == null || username.length() == 0) {
            Toast.makeText(context, "Username required.", Toast.LENGTH_SHORT).show();
            return ret;
        }
        if (password == null || password.length() == 0) {
            Toast.makeText(context, "Password required.", Toast.LENGTH_SHORT).show();
            return ret;
        }

        // Ensure username only contains valid characters
        if (username.contains("\\")) {
            //System.out.println("usernames may not contain \"\\\"");
            return ret;
        }
        if (username.contains("'")) {
            //System.out.println("usernames may not contain \"'\"");
            return ret;
        }

        // Ensure someone doesn't user the local user
        if (username.equals("default")) {
            //System.out.println("That username is taken.");
            return ret;
        }

        if (isNetworkConnected() && remote.addUser(username, password)) {
            //System.out.println("wtf");
            ret = local.addUser(username, password);
            //System.out.println("local addUser returned: " + ret);
            if (!ret) {
                //System.out.println("user '" + username + "' could not be added to the database. Check network connection.");
                while(!remote.deleteUser(username, password));
            }
        }

        return ret;
    }

    /**
     * delete the user from the database.
     *
     * @param username the user's username
     * @param password the associated password for the given username
     * @return true if the user is deleted; otherwise false.
     */
    public static boolean deleteUser(String username, String password) {
        boolean ret = true;

        if (local.deleteUser(username, password)) {
            if (!isNetworkConnected() || !remote.deleteUser(username, password)) {
                local.addUser(username, password);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Updated the given users password prodived that the oldPassword is correct.
     *
     * @param username    the user's username
     * @param oldPassword the user's old password
     * @param newPassword the user's new password
     * @return true if the passwoard was updated correctly; otherwise false.
     */
    public static boolean setPassword(String username, String oldPassword, String newPassword)
    {
        if (isNetworkConnected() && remote.setPassword(username, oldPassword, newPassword))
            return true;

        return false;
    }

    /**
     * Add a new friend to the give user's friend list.
     * @param usernameOwner the local user.
     * @param usernameFriend the friend's username
     * @return true if the friend is added; otherwise false
     */
    public static boolean addNewFriend(String usernameOwner, String usernameFriend) {
        if (isNetworkConnected()) {
            if (!remote.findUser(usernameFriend)) {
                //System.out.println("Sorry, we can't find that friend." + usernameFriend + ".");
                return false;
            }

            if (!local.addNewFriend(usernameOwner, usernameFriend)) {
                //System.out.println("Sorry, we were unable to add your friend to the local database.");
                return false;
            }

            if (remote.addNewFriend(usernameOwner, usernameFriend)) {
                return true;
            } else {
                //System.out.println("Sorry, we were unable to add your friend to the remote database.");
                local.deleteFriend(usernameOwner, usernameFriend);
            }
        } else {
            //System.out.println("Sorry, you need to connect to a network to use this feature.");
        }

        return false;
    }

    /**
     * Delete a friend from the given user's friend table.
     *
     * @param usernameOwner the local user.
     * @param usernameFriend the friend to delete.
     * @return true if deleted; otherwise false.
     */
    public static boolean deleteFriend(String usernameOwner, String usernameFriend) {
        if (!local.deleteFriend(usernameOwner, usernameFriend))
            return false;

        if (isNetworkConnected() && remote.deleteFriend(usernameOwner, usernameFriend))
            return true;

        return local.addNewFriend(usernameOwner, usernameFriend);
    }
    /* End shared database operations */

    /* Begin remote database operations */
    /**
     * Return the daily challenge block seed.
     *
     *
     * @return the block seed or -1 otherwise.
     */
    public static long getDailyChallengeSeed()
    {
        return remote.getDailyChallengeSeed();
    }

    /**
     * Return the block seed for the given user.
     *
     * @param username the username of the user.
     *
     * @return the block seed or -1 if not block seed exists for this user.
     */
    public static long getBlockSeed(String username)
    {
        if (isNetworkConnected())
            return remote.getBlockSeed(username);

        return -1;
    }

    /**
     * Returns the block seed for the given user.
     *
     * @param username the username of the user.
     * @param seed     the value of the new seed.
     */
    public static void setBlockSeed(String username, long seed)
    {
        if (isNetworkConnected()) {
            remote.setBlockSeed(username, seed);
        }
    }

    /**
     * Set the username and score of the friend with the highest score.
     *
     * @param username the local user's username
     * @return name,score of the top friend on success otherwise null
     */
    public static String[] getTopFriend(String username)
    {
        String name_score = "-1";

        if (isNetworkConnected()) {
            name_score = remote.getTopFriend(username);
        }

        return name_score.split(",");
    }

    /**
     * Returns true if the user is connected to the remote database and
     * their password matches that of the password stored on the remote
     * database.
     *
     * @param username the username of the user
     * @return true if the user is logged in; otherwise return false
     */
    public static boolean getLoginStatus(String username)
    {
        if (isNetworkConnected()) {
            return remote.getLoginStatus(username);
        }

        return false;
    }

    /**
     * Search the remote database for the given substring
     * @param subString the string to search for
     * @return the list of found userns on success; otherwise null
     */
    public static String[] searchUser(String subString) {
        if (isNetworkConnected()) {
            return remote.searchUser(subString);
        }
        //System.out.println("Check network conneciton");
        return null;
    }
    /* End remote database operations */

    /* Begin local database operations */
    /**
     * Get the given user's list of friends
     * @param username the user's username
     * @return an array list of friends on success; otherwise null.
     */
    public static ArrayList<String> getFriendsList(String username)
    {
        return local.getFriendsList(username);
    }

    /**
     * Get the local logged in user.
     *
     * @return the username of the logged in user, otherwise null
     */
    public static String getLocalLoggedInUser()
    {
        return local.getLocalLoggedInUser();
    }

    /**
     * Set the token for the given username.
     *
     * @param username the user's username
     * @param tok the associated token
     * @return true if the token was set; otherwise false.
     */
    public static boolean setToken(String username, String tok)
    {
        return local.setToken(username, tok);
    }

    /**
     * Set the token for the given username
     * @param username the user's username
     * @return the token on success, otherwise null
     */
    public static String getToken(String username)
    {
        return local.getToken(username);
    }
    /* End local database operations */
}
