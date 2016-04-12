package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface SharedDbOperations {

    String MASTER_DB = "Master.db";
    String MASTER_TABLE_NAME = "MASTER_TABLE";

    public void logout(Context context, String username);
    public boolean login(Context context, String username, String password);
    public void setHighScore(Context context, String username, int score);
    public int getHighScore(Context context, String username);
    public boolean addUser(Context context, String username, String password);
    public boolean setPassword(Context context, String username, String oldPassword, String newPassword);
    public boolean deleteUser(Context context, String username, String password);

}
