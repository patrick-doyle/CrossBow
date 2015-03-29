package com.crossbow.volley.mock;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.crossbow.volley.FileReader;
import com.crossbow.volley.FileRequest;

import java.io.IOException;

/**
 * Created by Patrick on 29/03/2015.
 */
public class MockFileReader implements FileReader {

    private boolean shouldFail = false;

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public byte[] readFile(FileRequest fileRequest) throws VolleyError {
        if(shouldFail) {
            throw new ParseError(new IOException());
        }
        return "mockdatarequest".getBytes();
    }
}
