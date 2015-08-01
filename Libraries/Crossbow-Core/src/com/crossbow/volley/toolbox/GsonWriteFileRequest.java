package com.crossbow.volley.toolbox;


import com.android.volley.VolleyError;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Patrick on 27/07/2015.
 */
public class GsonWriteFileRequest extends FileRequest<Boolean> {

    private final Object contents;
    private final FileResponse.Listener<Boolean> listener;
    private static final Gson gson = new Gson();

    public GsonWriteFileRequest(Object contents, String path, FileResponse.Listener<Boolean> listener, FileResponse.ErrorListener errorListener) {
        super(path, errorListener);
        this.contents = contents;
        this.listener = listener;
    }

    @Override
    public FileResponse<Boolean> doFileWork(File file) throws VolleyError {

        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
            JsonElement jsonElement = gson.toJsonTree(contents);
            gson.toJson(jsonElement, jsonWriter);
            return FileResponse.success(true);
        } catch (IOException e) {
            throw new VolleyError(e);
        }
    }

    @Override
    protected void deliverResult(Boolean result) {
        listener.onResponse(true);
    }
}
