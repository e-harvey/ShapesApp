package cse326.shapes_game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owner on 4/6/2016.
 */
public class LocalDbHandler extends SQLiteOpenHelper {

    String query;
    static final int DATABASE_VERSION = 1;

    public LocalDbHandler(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        String tableName = "MASTER_TABLE";
        query = "create table " + tableName + " (USERNAME TEXT PRIMARY KEY, PASSWORD TEXT, LOGIN INT, SCORE INTEGER) ";
        SQLiteDatabase db = this.getWritableDatabase();
    }


    public LocalDbHandler(Context context, String databaseName, boolean b) { // enter "true" for boolean to initiate friends db
        super(context, databaseName, null, DATABASE_VERSION);
        String tableName = "FRIENDS_TABLE";
        this.query = "create table " + tableName + " (ID INTEGER PRIMARY KEY,USERNAME TEXT)";
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) { // executes if database has not been created yet
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){ // executes on database change/upgrade
    }

}
