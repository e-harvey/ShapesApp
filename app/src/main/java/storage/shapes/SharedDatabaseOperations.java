package storage.shapes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Fritz on 4/11/2016.
 */
public abstract class SharedDatabaseOperations implements SharedDbOperations  {

    String databaseName;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // changes user password.  returns false if username username is already taken
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
