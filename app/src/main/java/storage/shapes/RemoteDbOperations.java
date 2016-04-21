package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface RemoteDbOperations {
    long getBlockSeed(String username);
    long getDailyChallengeSeed();
    void setBlockSeed(String username, long seed);
    boolean getLoginStatus(String username);
    String getTopFriend(String username);
    boolean findUser(String username);
}
