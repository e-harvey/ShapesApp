package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface RemoteDbOperations {

    String MASTER_DB = "YourDatabaseNameHere.db";
    String MASTER_TABLE_NAME = "YOUR_TABLE_NAME_HERE";

    public int getBlockSeed(String username);
    public void setBlockSeed(String username, int seed);
    public boolean getLoginStatus(String username);

}
