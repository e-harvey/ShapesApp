package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface SharedDbOperations {

    String MASTER_DB = "Master.db";
    String MASTER_TABLE_NAME = "MASTER_TABLE";

    /**
     * Log the user of the given database.
     *
     * @param username the user's username
     */
    public void logout(String username);

    /**
     * Log the user into the given database.
     *
     * @param username the user's username
     * @param password the given user's password
     * @return true if the user has been logged in; otherwise false.
     */
    public boolean login(String username, String password);

    /**
     * Set the highScore for the given user.
     *
     * @param username the user's username.
     * @param score    the new highscore.
     */
    public void setHighScore(String username, long score);

    /**
     * Get the highscore for the given user.
     *
     * @param username the user's username.
     * @return the highscore, or -1 on error.
     */
    public long getHighScore(String username);

    /**
     * The the given user to the given database.
     *
     * @param username the desired username
     * @param password the desicred password
     * @return true if the user is added to the database; otherwise false
     */
    public boolean addUser(String username, String password);

    /**
     * delete the user from the database.
     *
     * @param username the user's username
     * @param password the associated password for the given username
     * @return true if the user is deleted; otherwise false.
     */
    public boolean deleteUser(String username, String password);

    /**
     * Updated the given users password prodived that the oldPassword is correct.
     *
     * @param username    the user's username
     * @param oldPassword the user's old password
     * @param newPassword the user's new password
     * @return true if the passwoard was updated correctly; otherwise false.
     */
    public boolean setPassword(String username, String oldPassword, String newPassword);

}
