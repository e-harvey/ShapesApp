package storage.shapes;

import java.util.ArrayList;

interface LocalDbOperations {
    String getLocalLoggedInUser();
    ArrayList<String> getFriendsList(String username);
    boolean setToken(String username, String tok);
    String getToken(String username);
}
