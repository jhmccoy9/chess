package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

import javax.xml.crypto.Data;

public class ClearService {

    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException
    {
        dataAccess.clear();
        if (false)
            throw new DataAccessException("this ain't gonna work");
    }
}
