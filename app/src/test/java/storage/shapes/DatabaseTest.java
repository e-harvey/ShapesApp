package storage.shapes;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by eharvey on 4/21/16.
 */
public class DatabaseTest {
    @Test
    public void DatabaseOperationsTest() {
        boolean ret;
        long score, seed;
        String user = "foobarbach";
        String pass = "qwerty";
        String loggedIn;
        ArrayList<String> arrayList1 = new ArrayList<String>();
        ArrayList<String> arrayList2 = new ArrayList<String>();

        ret = DatabaseOperations.addUser(user, pass);
        assertEquals(true, ret);

        ret = DatabaseOperations.getLoginStatus(user);
        assertEquals(false, ret);

        DatabaseOperations.login(user, pass);

        ret = DatabaseOperations.getLoginStatus(user);
        assertEquals(true, ret);

        DatabaseOperations.setBlockSeed(user, 5555);

        seed = DatabaseOperations.getBlockSeed(user);
        assertEquals(5555, seed);

        DatabaseOperations.setHighScore(user, 1234);

        score = DatabaseOperations.getHighScore(user);
        assertEquals(1234, score);

        assertEquals(user, DatabaseOperations.getLocalLoggedInUser());

        arrayList2.clear();
        arrayList2.add(0, "foobarmozart");
        arrayList2.add(1, "barfoomozart");


        ret = DatabaseOperations.addUser("foobarmozart", pass);
        assertEquals(true, ret);

        ret = DatabaseOperations.addNewFriend(user, "barfoomozart");
        assertEquals(false, ret);

        ret = DatabaseOperations.addUser("barfoomozart", pass);
        assertEquals(true, ret);

        ret = DatabaseOperations.addNewFriend(user, "barfoomozart");
        assertEquals(true, ret);

        ret = DatabaseOperations.addNewFriend(user, "foobarmozart");
        assertEquals(true, ret);

        arrayList1.clear();
        arrayList1 = DatabaseOperations.getFriendsList(user);

        for (int i = 0; i < arrayList1.size(); i++) {
            System.out.println("frined: " + arrayList1.get(i));
        }

        ret = DatabaseOperations.login("foobarmozart", pass);
        assertEquals(true, ret);

        ret = DatabaseOperations.login("barfoomozart", pass);
        assertEquals(true, ret);

        DatabaseOperations.setHighScore("foobarmozart", 9999);
        DatabaseOperations.setHighScore("barfoomozart", 1111);

        loggedIn = DatabaseOperations.getLocalLoggedInUser();
        assertEquals(null, loggedIn);

        ret = DatabaseOperations.login(user, pass);
        assertEquals(true, ret);

        loggedIn = DatabaseOperations.getLocalLoggedInUser();
        assertEquals(user, loggedIn);

        String[] topGun = DatabaseOperations.getTopFriend(user);
        assertEquals("foobarmozart", topGun[1]);
        assertEquals(9999, (long) Long.valueOf(topGun[0]));

        ret = DatabaseOperations.setPassword(user, pass, "password");
        assertEquals(true, ret);

        ret = DatabaseOperations.deleteUser(user, pass);
        assertEquals(false, ret);

        ret = DatabaseOperations.deleteUser(user, "password");
        assertEquals(true, ret);

        ret = DatabaseOperations.deleteUser("foobarmozart", pass);
        assertEquals(true, ret);

        ret = DatabaseOperations.deleteUser("barfoomozart", pass);
        assertEquals(true, ret);
    }
}
