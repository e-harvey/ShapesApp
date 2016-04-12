package storage.shapes;

import android.content.Context;

/**
 * Created by Fritz on 4/7/2016.
 */
class RemoteDatabaseOperations extends SharedDatabaseOperations implements RemoteDbOperations {

    private Context context;

    RemoteDatabaseOperations(Context context) {
        this.context = context;
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

    public void setBlockSeed(String username, int seed) {
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // begin extended methods. these should be complete already

    // add user to database. returns false if username is already taken
    public boolean addUser(String username, String password) {
        return addUser(context, username, password);
    }

    // log user out of database
    public void logout(String username) {
        logout(context, username);
    }

    // set high score for user in database
    public void setHighScore(String username, int score) {
        setHighScore(context, username, score);
    }

    // return high score of user from database
    public int getHighScore(String username) {
        return getHighScore(context, username);
    }

    // logs a user into database
    public boolean login(String username, String password) {
        return login(context, username, password);
    }


    // reset the password.  will return false if old password does not match stored password
    public boolean setPassword(String username, String oldPassword, String newPassword) {
        return setPassword(context, username, oldPassword, newPassword);
    }

    // deletes user from local database.  returns false if password does not match.
    public boolean deleteUser(String username, String password) {
        return deleteUser(context, username, password);
    }

}
