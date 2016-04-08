package cse326.shapes_game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Owner on 4/6/2016.
 */
public class LocalDatabaseOperations implements SharedDbOperations, LocalDbOperations {
    String databaseName;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // returns an ArrayList including all friends for a particular user
    public ArrayList<String> getFriendsList(Context context, String username) {
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
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // changes user password.  returns false if username/password do not match stored data
    public boolean addUser(Context context, String username, String password){
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
    // adds a new friend to a user's friends list
    public void addNewFriend(Context context, String usernameOwner, String usernameFriend) {
        databaseName = usernameOwner + FRIENDS_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName,true);
        SQLiteDatabase SQ = localDB.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put("USERNAME",usernameFriend);
        SQ.insert(FRIENDS_TABLE_NAME, null, cVals);
        SQ.close();
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // deletes a friend from a user's friends list
    public void deleteFriend(Context context, String usernameOwner, String usernameFriend) {
        databaseName = usernameOwner + FRIENDS_DB;
        LocalDbHandler localDB = new LocalDbHandler(context, databaseName, true);
        SQLiteDatabase SQ = localDB.getWritableDatabase();
        String selection = "USERNAME" + " = ? ";
        String args[] = {usernameFriend};
        SQ.delete(FRIENDS_TABLE_NAME, selection, args); // delete user data from master table
        SQ.close();
    } //debugged

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void logout(Context context, String username){
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
    public void setHighScore(Context context, String username, int score){
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
    public int getHighScore(Context context, String username){
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
    public boolean login(Context context, String username, String password){
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



    public String getLoginStatus(Context context){
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



    public boolean setPassword(Context context, String username, String oldPassword, String newPassword){
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
    public boolean deleteUser(Context context, String username, String password){
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



}
