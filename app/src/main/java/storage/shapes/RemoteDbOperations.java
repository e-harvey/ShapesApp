package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface RemoteDbOperations {
    /**
     * Return the block seed for the given user.
     *
     * @param username the username of the user.
     *
     * @return the block seed or -1 if not block seed exists for this user.
     */
    public long getBlockSeed(String username);

    /**
     * Set the block seed for the given user.
     *
     * @param username the username of the user.
     * @param seed     the value of the new seed.
     */
    public void setBlockSeed(String username, long seed);

    /**
     * Returns true if the user is connected to the remote database and
     * their password matches that of the password stored on the remote
     * database.
     *
     * @param username the username of the user
     * @return true if the user is logged in; otherwise return false
     */
    public boolean getLoginStatus(String username);

}
