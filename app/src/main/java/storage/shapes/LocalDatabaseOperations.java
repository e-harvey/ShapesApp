package storage.shapes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
class LocalDatabaseOperations implements LocalDbOperations, SharedDbOperations  {
    private String databaseName;
    private Context context;

    LocalDatabaseOperations(Context context) {
        this.context = context;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // returns an ArrayList including all friends for a particular user
    public ArrayList<String> getFriendsList(String username) {
        ArrayList<String> friendsList = new ArrayList<>();
        databaseName = username + FRIENDS_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName, true);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"USERNAME"};
        Cursor cursor = SQ.query(FRIENDS_TABLE_NAME, columns, null, null, null, null, null);
        String name;
        cursor.moveToFirst();
        int count = 0;
        do {
            name = cursor.getString(0);
            friendsList.add(count, name);
            count++;
        } while (cursor.moveToNext());
        cursor.close();
        return friendsList;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // adds a new friend to a user's friends list
    public void addNewFriend(String usernameOwner, String usernameFriend) {
        databaseName = usernameOwner + FRIENDS_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName, true);
        SQLiteDatabase SQ = localDB.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put("USERNAME", usernameFriend);
        SQ.insert(FRIENDS_TABLE_NAME, null, cVals);
        SQ.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // deletes a friend from a user's friends list
    public void deleteFriend(String usernameOwner, String usernameFriend) {
        databaseName = usernameOwner + FRIENDS_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName, true);
        SQLiteDatabase SQ = localDB.getWritableDatabase();
        String selection = "USERNAME" + " = ? ";
        String args[] = {usernameFriend};
        SQ.delete(FRIENDS_TABLE_NAME, selection, args); // delete user data from master table
        SQ.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // changes user password.  returns false if username username is already taken
    private boolean addUserLocal(Context context, String username, String password){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getWritableDatabase();
        String[] columns = {"USERNAME"};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, null, null, null, null, null);
        String name;
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(0);
                if (name.equals(username)) {
                    return false;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        ContentValues cVals = new ContentValues();
        cVals.put("USERNAME", username);
        cVals.put("PASSWORD", password);
        cVals.put("LOGIN", 0);
        cVals.put("SCORE", 0);
        SQ.insert(MASTER_TABLE_NAME, null, cVals);
        SQ.close();
        return true;
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void logoutLocal(Context context, String username){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put("LOGIN", 0);
        String selection = "USERNAME" + " = ? ";
        String args[] = {username};
        SQ.update(MASTER_TABLE_NAME, cVals, selection, args);
        SQ.close();
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setHighScoreLocal(Context context, String username, long score){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put("SCORE", score);
        String selection = "USERNAME" + " = ? ";
        String args[] = {username};
        SQ.update(MASTER_TABLE_NAME, cVals, selection, args);
        SQ.close();
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private long getHighScoreLocal(Context context, String username){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"SCORE"};
        String selection = "USERNAME" + " = ? ";
        String args[] = {username};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, selection, args, null, null, null);
        String score = cursor.getString(0);
        int score1 = Integer.valueOf(score);
        return score1;
    } //

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // logs in user. returns false if username and/or password do not match stored values
    private boolean loginLocal(Context context, String username, String password){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"USERNAME","PASSWORD"};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, null, null, null, null, null);
        String USER, PASSWORD;
        cursor.moveToFirst();
        do {
            USER = cursor.getString(0);
            PASSWORD = cursor.getString(1);
            if (USER.equals(username)) {
                if (PASSWORD.equals(password)) {
                    SQ.close();
                    SQ = localDB.getWritableDatabase();
                    ContentValues cVals = new ContentValues();
                    cVals.put("LOGIN", 1);
                    String selection = "USERNAME" + " = ? ";
                    String args[] = {username};
                    SQ.update(MASTER_TABLE_NAME, cVals, selection, args);
                    SQ.close();
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    SQ.close();
                    return false;
                }
            }
        } while (cursor.moveToNext());
        return false;
    } //debugged

    private boolean setPasswordLocal(Context context, String username, String oldPassword, String newPassword){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"USERNAME","PASSWORD"};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, null, null, null, null, null);
        String USER, PASSWORD;
        cursor.moveToFirst();
        do {
            USER = cursor.getString(0);
            PASSWORD = cursor.getString(1);
            if (USER.equals(username)) {
                if (PASSWORD.equals(oldPassword)) {
                    SQ.close();
                    SQ = localDB.getWritableDatabase();
                    ContentValues cVals = new ContentValues();
                    cVals.put("PASSWORD", newPassword);
                    String selection = "USERNAME" + " = ? ";
                    String args[] = {username};
                    SQ.update(MASTER_TABLE_NAME, cVals, selection, args);
                    SQ.close();
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    SQ.close();
                    return false;
                }
            }
        } while (cursor.moveToNext());
        return false;
    } // debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // deletes user.  returns false if username/password do not match stored data
    private boolean deleteUserLocal(Context context, String username, String password){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"USERNAME","PASSWORD"};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, null, null, null, null, null);
        String USER, PASSWORD;
        cursor.moveToFirst();
        do {
            USER = cursor.getString(0);
            PASSWORD = cursor.getString(1);
            if (USER.equals(username)) {
                if (PASSWORD.equals(password)) {
                    SQ.close();
                    SQ = localDB.getWritableDatabase();
                    String selection = "USERNAME" + " = ? ";
                    String args[] = {username};
                    SQ.delete(MASTER_TABLE_NAME, selection, args);
                    SQ.close();
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    SQ.close();
                    return false;
                }
            }
        } while (cursor.moveToNext());
        return false;
    } //debugged

    // add user to local database. returns false if username is already taken
    public boolean addUser(String username, String password) {
        return addUserLocal(context, username, password);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // returns locally logged in user from previous session. returns null if no users currently logged in.
    public String getLocalLoggedInUser(){
        databaseName = MASTER_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName, true);
        SQLiteDatabase SQ = localDB.getReadableDatabase();
        String[] columns = {"USERNAME","LOGIN"};
        Cursor cursor = SQ.query(MASTER_TABLE_NAME, columns, null, null, null, null, null);
        String loggedUser = null;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int logStatus = Integer.valueOf(cursor.getString(1));
                if (logStatus == 1) {
                    loggedUser = name;
                    cursor.close();
                    SQ.close();
                    return loggedUser;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        SQ.close();
        return loggedUser;
    } //debugged

    // log user out of local database
    public void logout(String username) {
        logoutLocal(context, username);
    }

    // set high score for user in local database
    public void setHighScore(String username, long score) {
        setHighScoreLocal(context, username, score);
    }

    // return high score of user from local database
    public long getHighScore(String username) {
        return getHighScoreLocal(context, username);
    }

    // log a user into local database.  user will stay logged in until logout method is called
    public boolean login(String username, String password) {
        return loginLocal(context, username, password);
    }

    // reset the password.  will return false if old password does not match stored password
    public boolean setPassword(String username, String oldPassword, String newPassword) {
        return setPasswordLocal(context, username, oldPassword, newPassword);
    }

    // deletes user from local database.  returns false if password does not match.
    public boolean deleteUser(String username, String password) {
        return deleteUserLocal(context, username, password);
    }
}
