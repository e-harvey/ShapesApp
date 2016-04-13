package storage.shapes;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by eharvey on 4/12/16.
 */
public class RemoteDatabaseTest {
    @Test
    public void RemoteDatabaseOperationsTest() {
        long seed;
        boolean ret;

        RemoteDatabaseOperations remoteDatabaseOperations = new RemoteDatabaseOperations();
        assertNotEquals(null, remoteDatabaseOperations);

        //remoteDatabaseOperations.getLoginStatus("testuser");

        remoteDatabaseOperations.setBlockSeed("testuser", 1111);

        seed = remoteDatabaseOperations.getBlockSeed("testuser");
        assertEquals(1111, seed);

        ret = remoteDatabaseOperations.addUser("testuser2", "password");
        assertEquals(true, ret);

        ret = remoteDatabaseOperations.addUser("testuser2", "password");
        assertEquals(false, ret);
    }

}
