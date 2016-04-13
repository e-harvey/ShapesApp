package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/7/2016.
 */
public class DatabaseOperations  {


    Context context;
    LocalDatabaseOperations local;
    RemoteDatabaseOperations remote;

    public DatabaseOperations(Context context) {
        this.context = context;
        LocalDatabaseOperations local = new LocalDatabaseOperations(context);
        RemoteDatabaseOperations remote = new RemoteDatabaseOperations();
    }

    // logs user out of databases
    public void logout(String username) {
        local.logout(username);
        remote.logout(username);
    }

    // logs user into databases. returns false if password/username do not match
    public boolean login(String username, String password) {
        return local.login(username, password);
    }

    // returns username of local user logged in from previous session. returns null if no users logged in.
    public String getLocalLoggedInUser() {
        return local.getLocalLoggedInUser();
    }

    // set high score for user
    public void setHighScore(String username, int score) {
        local.setHighScore(username, score);
        remote.setHighScore(username, score);
    }

    // return high score for user. returns integer high score
    public int getHighScore(String username){
        return local.getHighScore(username);

    }

    // add username to databases. returns false if username is already taken
    public boolean addUser(String username, String password) {
        boolean ret = local.addUser(username, password) | remote.addUser(username, password);
        return ret;
    }

    // resets password for user. returns false if password does not match
    public boolean setPassword(String username, String oldPassword, String newPassword) {
        return local.setPassword(username, oldPassword, newPassword);
    }

    // deletes user from system. returns false if password does not match
    public boolean deleteUser(String username, String password) {
        remote.deleteUser(username, password);
        return local.deleteUser(username, password);
    }

    // returns an ArrayList including all friends for a particular user
    public ArrayList<String> getFriendsList(String username) {
        return local.getFriendsList(username);
    }

    // adds a new friend to a user's friends list
    public void addNewFriend(String usernameOwner, String usernameFriend) {
        local.addNewFriend(usernameOwner, usernameFriend);
    }

    // deletes a friend from a user's friends list
    public void deleteFriend(String usernameOwner, String usernameFriend) {
        local.deleteFriend(usernameOwner, usernameFriend);
    }
}
