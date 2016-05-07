package storage.shapes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

class LocalDatabaseOperations implements LocalDbOperations, SharedDbOperations  {
    private LocalDbHandler localDbHandler;
    private SQLiteDatabase sqLiteDatabase;
    private String sqlCmd;
    Cursor cursor;

    /* Begin constructors */
    LocalDatabaseOperations(Context context) {
        localDbHandler = new LocalDbHandler(context, "shapes", 1);
        sqLiteDatabase = localDbHandler.getWritableDatabase(); // users database helper class to manage database
    }
    /* End constructors */

    /* Begin local database operations */

    /**
     * Return the list of username of friends for a user.
     *
     * @param username the user's username
     * @return ArrayList<String> of friends
     */
    public ArrayList<String> getFriendsList(String username)
    {
        String col[] = {"friend"};
        String selArgs[] = {username};

        int i = 0;
        ArrayList<String> arrayList = new ArrayList<String>();

        cursor = sqLiteDatabase.query("friends", col, "user = ?", selArgs, null, null, null);

        //System.out.println("count: " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(i++, cursor.getString(0));
            } while (cursor.moveToNext());

            return arrayList;
        } else {
            return null;
        }
    }


    /**
     * Returns the name of the username currently logged in, or the user logged in from previous session.
     *
     * @return String of username.
     */
    public String getLocalLoggedInUser()
    {
        String col[] = {"username, status"};
        String selArgs[] = {"1"};

        cursor = sqLiteDatabase.query("user", col, "status = ?", selArgs, null, null, null);

        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            return cursor.getString(0);
        }

        return null;
    }

    /**
     * Set the token for the given username.
     *
     * @param username the user's username
     * @param tok the associated token
     * @return true if the token was set; otherwise false.
     */
    public boolean setToken(String username, String tok)
    {
        sqlCmd = "update user " +
                "set token = '" + tok + "'" +
                "where username = '" + username + "'";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;
    }

    /**
     * Get the token for the given username.
     *
     * @param username the user's username
     * @return String of token.
     */
    public String getToken(String username)
    {
        String col[] = {"token"};
        String selArgs[] = {username};

        cursor = sqLiteDatabase.query("user", col, "username = ?", selArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getString(0);
        }

        return null;
    }


    /**
     * Adds username of friend to database.
     *
     * @param usernameOwner the user's username
     * @param  usernameFriend the friend's username
     * @return true if the friend was added; otherwise false.
     */
    public boolean addNewFriend(String usernameOwner, String usernameFriend)
    {
        sqlCmd = "insert into friends " +
                "values ('" + usernameOwner +
                "', '" + usernameFriend + "')";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;
    }

    /**
     * Set the token for the given username.
     *
     * @param usernameOwner the user's username
     * @param usernameFriend of friend's username
     * @return true if friend was removed; otherwise false.
     */
    public boolean deleteFriend(String usernameOwner, String usernameFriend)
    {
        sqlCmd = "delete from friends " +
                "where user = '" + usernameOwner + "' AND " +
                "friend = '" + usernameFriend + "'";


        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;
    }

    /**
     * Logs user out of local database
     *
     * @param username the user's username
     * @return void
     */
    public void logout(String username)
    {
        sqlCmd = "update user" +
                " set status = 0 where token = '" + this.getToken(username) + "'" +
                " AND username = '" + username + "'";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            ;
        }
    }

    /**
     * Logs user into local database
     *
     * @param username the user's username
     * @param password the user's password
     * @return true if success; false if not
     */
    public boolean login(String username, String password)
    {
        String col[] = {"username"};
        String selArgs[] = {username};


        // Ensure there is only ever 1 local user logged in at a time
        sqlCmd = "update user" +
                " set status = 0";

        sqLiteDatabase.execSQL(sqlCmd);

        cursor = sqLiteDatabase.query("user", col, "username = ?", selArgs, null, null, null);

        if (cursor.getCount() > 0) {
            sqlCmd = "update user " +
                    "set status = 1 " +
                    "where username = '" + username + "'";

            try {
                sqLiteDatabase.execSQL(sqlCmd);
            } catch (SQLiteConstraintException e) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Set high score into local database
     *
     * @param username the user's username
     * @param score from gameplay
     * @return void
     */
    public void setHighScore(String username, long score)
    {
        sqlCmd = "update user " +
                "set highscore = '" + score +
                "' where username = '" + username + "'";

            try {
                sqLiteDatabase.execSQL(sqlCmd);
            } catch (SQLiteConstraintException e) {
                ;
            }
        //System.out.println("Setting high score for " + username + " to " + score + ".");
    }

    /**
     * Returns high score for specified user
     *
     * @param username the user's username
     * @return long format score
     */
    public long getHighScore(String username)
    {
        String col[] = {"highscore"};
        String selArgs[] = {username};

        cursor = sqLiteDatabase.query("user", col, "username = ?", selArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return Long.valueOf(cursor.getString(0));
        } else {
            return -1;
        }

    }

    /**
     * Adds username to local database
     *
     * @param username the user's username
     * @param password the user's password
     * @return true if successful; false if not
     */
    public boolean addUser(String username, String password)
    {
        sqlCmd = "insert into user " +
                "values ('" + username + "', '" +
                "0', '0', 'beefdeaddeadbeef')";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Removes user completely from local database
     *
     * @param username the user's username
     * @param password the user's password
     * @return boolean true if succesful; false if not
     */
    public boolean deleteUser(String username, String password)
    {
        String col[] = {"username"};
        String selArgs[] = {username};

        cursor = sqLiteDatabase.query("user", col, "username = ?", selArgs, null, null, null);

        if (cursor.getCount() <= 0) {
            return false;
        } else {
            sqlCmd = "delete from user " +
                    "where username = '" + username + "'";

            try {
                sqLiteDatabase.execSQL(sqlCmd);
            } catch (SQLiteConstraintException e) {
                ;
            }

            sqlCmd = "delete from friends " +
                    "where user = '" + username + "'";

            try {
                sqLiteDatabase.execSQL(sqlCmd);
            } catch (SQLiteConstraintException e) {
                ;
            }

            return true;
        }
    }

    /**
     * Sets new password for a user
     *
     * @param username the user's username
     * @param oldPassword the previous password the user wishes to change
     * @param oldPassword the new password the user wishes to enter
     * @return boolean true if successful; false if not
     */
    public boolean setPassword(String username, String oldPassword, String newPassword)
    {
        return true;
    }
    /* End local database operations */
}
