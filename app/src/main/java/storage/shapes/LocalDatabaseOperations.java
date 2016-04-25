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

/**
 * Created by Fritz on 4/6/2016.
 */
class LocalDatabaseOperations implements LocalDbOperations, SharedDbOperations  {
    private LocalDbHandler localDbHandler;
    private SQLiteDatabase sqLiteDatabase;
    private String sqlCmd;
    Cursor cursor;

    /* Begin constructors */
    LocalDatabaseOperations(Context context) {
        localDbHandler = new LocalDbHandler(context, "shapes", 1);
        sqLiteDatabase = localDbHandler.getWritableDatabase();
    }
    /* End constructors */

    /* Begin local database operations */
    public ArrayList<String> getFriendsList(String username)
    {
        String col[] = {"friend"};
        String selArgs[] = {username};

        int i = 0;
        ArrayList<String> arrayList = new ArrayList<String>();

        cursor = sqLiteDatabase.query("friends", col, "user = ?", selArgs, null, null, null);

        cursor.moveToFirst();
        do {
            arrayList.add(i++, cursor.getString(0));
        } while (cursor.moveToNext());

        return arrayList;
    }

    public String getLocalLoggedInUser()
    {
        String col[] = {"username, status"};
        String selArgs[] = {"1"};

        cursor = sqLiteDatabase.query("user", col, "status = ?", selArgs, null, null, null);

        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            return cursor.getString(0);
        }

        // Ensure there is only ever 1 local user logged in at a time
        sqlCmd = "update user" +
                " set status = 0";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            ;
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
    /* End local database operations */

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

    /* Begin shared database operations */
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

    public boolean login(String username, String password)
    {
        String col[] = {"username"};
        String selArgs[] = {username};

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
    }

    public long getHighScore(String username)
    {
        String col[] = {"highscore"};
        String selArgs[] = {username};

        cursor = sqLiteDatabase.query("user", col, "username = ?", selArgs, null, null, null);

        cursor.moveToNext();
        return Long.valueOf(cursor.getString(0));
    }

    public boolean addUser(String username, String password)
    {
        sqlCmd = "insert into user " +
                "values ('" + username + "', '" +
                "0', '0', 'beefdeaddeadbeef')";

        try {
            sqLiteDatabase.execSQL(sqlCmd);
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;
    }

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

    public boolean setPassword(String username, String oldPassword, String newPassword)
    {
        return true;
    }
    /* End shared database operations */
}
