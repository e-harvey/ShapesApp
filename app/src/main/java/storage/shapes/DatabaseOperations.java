package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/7/2016.
 */
public class DatabaseOperations  {


    Context context;

    public DatabaseOperations(Context context) {
        this.context = context;
    }

    // logs user out of databases
    public void logout(String username) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);

        local.logout(username);
        remote.logout(username);
    }

    // logs user into databases. returns false if password/username do not match
    public boolean login(Context context, String username, String password) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);

        return local.login(username, password);
    }

    // returns username of local user logged in from previous session. returns null if no users logged in.
    public String getLocalLoggedInUser() {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        return local.getLocalLoggedInUser();
    }

    // set high score for user
    public void setHighScore(String username, int score) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);

        local.setHighScore(username, score);
        remote.setHighScore(username, score);
    }

    // return high score for user. returns integer high score
    public int getHighScore(String username){
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);

        return local.getHighScore(username);

    }

    // add username to databases. returns false if username is already taken
    public boolean addUser(String username, String password) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);
        return false;
    }

    // resets password for user. returns false if password does not match
    public boolean setPassword(String username, String oldPassword, String newPassword) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);
        return false;
    }

    // deletes user from system. returns false if password does not match
    public boolean deleteUser(String username, String password) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);
        return false;
    }

    // returns an ArrayList including all friends for a particular user
    public ArrayList<String> getFriendsList(String username) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        return local.getFriendsList(username);
    }

    // adds a new friend to a user's friends list
    public void addNewFriend(String usernameOwner, String usernameFriend) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        local.addNewFriend(usernameOwner, usernameFriend);
    }

    // deletes a friend from a user's friends list
    public void deleteFriend(String usernameOwner, String usernameFriend) {
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        local.deleteFriend(usernameOwner, usernameFriend);
    }

    public int getBlockSeed(String username) {
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);
        return 0;
    }

    public void setBlockSeed(String username, int seed) {
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations(context);
    }

}
