package com.crossbow.volley.mock;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.crossbow.volley.FileReadRequest;
import com.crossbow.volley.FileReadStreamRequest;
import com.crossbow.volley.FileStack;
import com.crossbow.volley.FileWriteRequest;
import com.crossbow.volley.FileWriteStreamRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Patrick on 29/03/2015.
 */
public class MockFileStack implements FileStack {

    private boolean shouldFail = false;
    public String writtenData;

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public byte[] readFileData(FileReadRequest fileRequest) throws VolleyError {
        if(shouldFail) {
            throw new ParseError(new IOException());
        }
        return "mockdatarequest".getBytes();
    }

    @Override
    public InputStream readFileStream(FileReadStreamRequest fileRequest) throws VolleyError {
        return new ByteArrayInputStream("mockdatarequest".getBytes());
    }

    @Override
    public OutputStream writeFileStream(FileWriteStreamRequest fileRequest) throws VolleyError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writtenData = new String(outputStream.toByteArray());
        return outputStream;
    }

    @Override
    public void writeFileData(FileWriteRequest fileRequest) throws VolleyError {
        writtenData = new String(fileRequest.getData());
    }
}
