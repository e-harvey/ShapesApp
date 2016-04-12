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
class LocalDatabaseOperations extends SharedDatabaseOperations implements LocalDbOperations {
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

    // add user to local database. returns false if username is already taken
    public boolean addUser(String username, String password) {
        return addUser(context, username, password);
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
        logout(context, username);
    }

    // set high score for user in local database
    public void setHighScore(String username, int score) {
        setHighScore(context, username, score);
    }

    // return high score of user from local database
    public int getHighScore(String username) {
        return getHighScore(context, username);
    }

    // log a user into local database.  user will stay logged in until logout method is called
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
