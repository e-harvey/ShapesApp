package storage.shapes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owner on 4/6/2016.
 */
public class LocalDbHandler extends SQLiteOpenHelper {

    private String sqlCmd;

    public LocalDbHandler(Context context, String databaseName, int databaseVersion)
    {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
