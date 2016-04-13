package storage.shapes;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by eharvey on 4/12/16.
 */
public class RemoteDatabaseTest {
    @Test
    public void RemoteDatabaseOperationsTest() {
        RemoteDatabaseOperations remoteDatabaseOperations = new RemoteDatabaseOperations();
        assertNotEquals(null, remoteDatabaseOperations);
    }

}
