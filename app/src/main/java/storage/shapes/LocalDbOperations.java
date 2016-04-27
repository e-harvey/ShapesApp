package storage.shapes;

import java.util.ArrayList;

interface LocalDbOperations {
    String getLocalLoggedInUser();
    boolean setToken(String username, String tok);
    String getToken(String username);
}
