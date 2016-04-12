package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Fritz on 4/6/2016.
 */
interface LocalDbOperations {

    String FRIENDS_TABLE_NAME = "FRIENDS_TABLE";
    String FRIENDS_DB = "Friends.db";

    public ArrayList<String> getFriendsList(String username);
    public void addNewFriend(String usernameOwner, String usernameFriend);
    public void deleteFriend(String usernameOwner, String usernameFriend);
    public String getLocalLoggedInUser();

}
