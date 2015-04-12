package com.crossbow.volley;

import com.android.volley.VolleyError;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Patrick on 24/03/2015.
 *
 * Reads and writes the files based on the file requests
 *
 */
public interface FileStack {

    public byte[] readFileData(FileReadRequest fileRequest) throws VolleyError;

    public InputStream readFileStream(FileReadStreamRequest fileRequest) throws VolleyError;

    public OutputStream writeFileStream(FileWriteStreamRequest fileRequest) throws VolleyError;

    public void writeFileData(FileWriteRequest fileRequest) throws VolleyError;
}
