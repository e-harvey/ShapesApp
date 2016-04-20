package storage.shapes;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by eharvey on 4/12/16.
 */
public class RemoteDatabaseTest {
    @Test
    public void RemoteDatabaseOperationsTest() {
        long seed, score;
        boolean ret;

        RemoteDatabaseOperations remoteDatabaseOperations = new RemoteDatabaseOperations();
        assertNotEquals(null, remoteDatabaseOperations);

        ret = remoteDatabaseOperations.addUser("testuser", "password");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.addUser("testuser2", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.login("testuser2", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.getLoginStatus("testuser2");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.setPassword("testuser2", "password2", "password");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.setPassword("testuser2", "password", "password2");
        assertEquals(true, ret);

        remoteDatabaseOperations.setBlockSeed("testuser2", 5555);

        seed = remoteDatabaseOperations.getBlockSeed("testuser2");
        assertEquals(seed, 5555);

        remoteDatabaseOperations.setHighScore("testuser2", 5555);

        score = remoteDatabaseOperations.getHighScore("testuser2");
        assertEquals(5555, score);

        remoteDatabaseOperations.logout("testuser2");

        ret = remoteDatabaseOperations.getLoginStatus("testuser2");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.deleteUser("testuser2", "blah");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.deleteUser("testuser2", "password2");
        assertEquals(true, ret);
        //dailychallenge seed = 21080 at Tue Apr 19 22:41:59 EDT 2016

        seed = remoteDatabaseOperations.getDailyChallengeSeed();
        assertEquals(21080, seed);
    }

}
