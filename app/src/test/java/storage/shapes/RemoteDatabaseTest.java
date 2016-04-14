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

        remoteDatabaseOperations.setPassword("testuser", "password", "testhash");

        remoteDatabaseOperations.login("testuser", "testhash");

        ret = remoteDatabaseOperations.getLoginStatus("testuser");
        assertEquals(true, ret);

        remoteDatabaseOperations.logout("testuser");

        ret = remoteDatabaseOperations.getLoginStatus("testuser");
        assertEquals(false, ret);

        remoteDatabaseOperations.login("testuser", "testhash");

        /******************************************************************************************/
        /******************************************************************************************/

        remoteDatabaseOperations.setBlockSeed("testuser", 1234);

        seed = remoteDatabaseOperations.getBlockSeed("testuser");
        assertEquals(1234, seed);

        remoteDatabaseOperations.setBlockSeed("testuser", 4321);

        /******************************************************************************************/

        ret = remoteDatabaseOperations.addUser("testuser2", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.addUser("testuser2", "password");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.deleteUser("testuser2", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.deleteUser("testuser2", "password");
        assertEquals(false, ret);

        /******************************************************************************************/

        remoteDatabaseOperations.setHighScore("testuser", 123456);

        score = remoteDatabaseOperations.getHighScore("testuser");
        assertEquals(123456, score);

        remoteDatabaseOperations.setHighScore("testuser", 0);

        /******************************************************************************************/

        ret = remoteDatabaseOperations.setPassword("testuser", "testhash", "password");
        assertEquals(true, ret);

        remoteDatabaseOperations.logout("testuser");

        ret = remoteDatabaseOperations.login("testuser", "password");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.login("testuser", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.setPassword("testuser", "dummy", "dummy");
        assertEquals(false, ret);

        ret = remoteDatabaseOperations.setPassword("testuser", "password", "testhash");
        assertEquals(true, ret);

        remoteDatabaseOperations.logout("testuser");
    }

}
