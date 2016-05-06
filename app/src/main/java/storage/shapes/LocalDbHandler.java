package storage.shapes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LocalDbHandler extends SQLiteOpenHelper {

    private String sqlCmd;

    public LocalDbHandler(Context context, String databaseName, int databaseVersion)
    {
        super(context, databaseName, null, databaseVersion);
    }


    @Override
    /**
     * Creates database or table is not already present
     *
     * @param SQLiteDatabase local database
     * @return void
     */
    public void onCreate(SQLiteDatabase db)
    {
        sqlCmd = "create table user(" +
                "username varchar(15), " +
                "highscore bigint unsigned, " +
                "status boolean, " +
                "token char(60), " +
                "primary key (username))";

        // Create the user table
        db.execSQL(sqlCmd);


        sqlCmd = "create table friends(" +
                "user varchar(15), " +
                "friend varchar(15), " +
                "constraint user_friend_unique unique(user,friend))";

        // Create the friend table
        db.execSQL(sqlCmd);

        sqlCmd = "insert into user " +
                "values ('null', '0', '1', 'deadbeefdead')";

        // Create the default local user
        db.execSQL(sqlCmd);
    }

    @Override
    /**
     * Upgrades tables
     *
     * @param SQLiteDatabase local database
     * @param oldVersion previous version number
     * @param newVersion new version number

     * @return void
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
