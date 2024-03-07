package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class dataAccessTest {
    static final DataAccess dataAccess;
    static {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clearTest()
    {
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }


}