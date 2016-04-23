package storage.shapes;

import android.content.Context;

import java.util.ArrayList;

interface SharedDbOperations {
    void logout(String username);
    boolean login(String username, String password);
    void setHighScore(String username, long score);
    long getHighScore(String username);
    boolean addUser(String username, String password);
    boolean deleteUser(String username, String password);
    boolean setPassword(String username, String oldPassword, String newPassword);
    boolean addNewFriend(String usernameOwner, String usernameFriend);
    boolean deleteFriend(String usernameOwner, String usernameFriend);
}
