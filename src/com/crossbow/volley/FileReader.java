package com.crossbow.volley;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Patrick on 24/03/2015.
 */
public interface FileReader {

    public byte[] readFile(FileRequest fileRequest) throws VolleyError;
}
