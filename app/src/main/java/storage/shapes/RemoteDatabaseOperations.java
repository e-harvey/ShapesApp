/**
 * Remote storage method implementations.
 */

package storage.shapes;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class RemoteDatabaseOperations implements RemoteDbOperations, SharedDbOperations  {

    /* Begin constructors */
    RemoteDatabaseOperations() {}
    /* End constructors */

    /* Begin RemoteDbOperation methods */
    public boolean getLoginStatus(String username)
    {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getLoginStatus");

        params.add(4, "username");
        params.add(5, username);

        try {
            ret = new SendPostRequest().execute(params).get().equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public long getBlockSeed(String username)
    {
        long ret = -1;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getBlockSeed");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, DatabaseOperations.getToken(username));

        try {
            ret = Long.valueOf(new SendPostRequest().execute(params).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public long getDailyChallengeSeed()
    {
        long ret = -1;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getDailyChallengeSeed");

        try {
            ret = Long.valueOf(new SendPostRequest().execute(params).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void setBlockSeed(String username, long seed)
    {
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setBlockSeed");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, DatabaseOperations.getToken(username));

        params.add(8, "seed");
        params.add(9, String.valueOf(seed));

        new SendPostRequest().execute(params);
    }

    public String getTopFriend(String username) {
        String ret = "-1";
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getTopFriend");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, DatabaseOperations.getToken(username));

        try {
            ret = new SendPostRequest().execute(params).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean findUser(String username) {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "findUser");

        params.add(4, "username");
        params.add(5, username);

        try {
            ret = Boolean.valueOf(new SendPostRequest().execute(params).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }
    /* End RemoteDbOperation methods */

    /* Begin SharedDatabaseOperations methods */
    public boolean addUser(String username, String password)
    {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "addUser");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        try {
            ret = new SendPostRequest().execute(params).get().equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public boolean deleteUser(String username, String password) {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "deleteUser");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        try {
            ret = new SendPostRequest().execute(params).get().equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public void setHighScore(String username, long score)
    {
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setHighScore");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, DatabaseOperations.getToken(username));

        params.add(8, "score");
        params.add(9, String.valueOf(score));

        new SendPostRequest().execute(params);
    }

    public long getHighScore(String username)
    {
        long ret = -1;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getHighScore");

        params.add(4, "username");
        params.add(5, username);

        try {
            ret = Long.valueOf(new SendPostRequest().execute(params).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean login(String username, String password)
    {
        boolean ret = false;

        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "login");

        params.add(2, "action");
        params.add(3, "login");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        try {
            String token = new SendPostRequest().execute(params).get();
            if (!(ret = token.equals("-1"))) {
                DatabaseOperations.setToken(username, token);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public void logout(String username)
    {
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "login");

        params.add(2, "action");
        params.add(3, "logout");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, DatabaseOperations.getToken(username));

        new SendPostRequest().execute(params);
    }

    public boolean setPassword(String username, String oldPassword, String newPassword)
    {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setPassword");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "oldPassword");
        params.add(7, oldPassword);

        params.add(8, "newPassword");
        params.add(9, newPassword);

        params.add(10, "token");
        params.add(11, DatabaseOperations.getToken(username));

        try {
            String tmp = new SendPostRequest().execute(params).get();

            if (tmp != null)
                ret = tmp.equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public boolean addNewFriend(String usernameOwner, String usernameFriend)
    {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "addNewFriend");

        params.add(4, "usernameOwner");
        params.add(5, usernameOwner);

        params.add(6, "usernameFriend");
        params.add(7, usernameFriend);

        params.add(8, "token");
        params.add(9, DatabaseOperations.getToken(usernameOwner));

        try {
            ret = new SendPostRequest().execute(params).get().equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return !ret;
    }

    public boolean deleteFriend(String usernameOwner, String usernameFriend)
    {
        boolean ret = false;
        ArrayList<String> params = new ArrayList<String>();

        // Pack the post request
        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "deleteFriend");

        params.add(4, "usernameOwner");
        params.add(5, usernameOwner);

        params.add(6, "usernameFriend");
        params.add(7, usernameFriend);

        try {
            ret = new SendPostRequest().execute(params).get().equals("-1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }
    /* End SharedDatabaseOperations methods */
}
