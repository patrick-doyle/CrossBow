package com.crossbow.volley.toolbox;


import com.android.volley.VolleyError;
import com.crossbow.volley.FileRequest;
import com.crossbow.volley.FileResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

public abstract class GsonReadFileRequest<T> extends FileRequest<T> {

    private final FileResponse.Listener<T> listener;
    private static final Gson gson = new Gson();
    private final Type type;

    public GsonReadFileRequest(String path, FileResponse.Listener<T> listener, FileResponse.ErrorListener errorListener) {
        super(path, errorListener);
        this.listener = listener;
        this.type = new TypeToken<T>(){}.getType();
    }

    @Override
    public FileResponse<T> doFileWork(File file) throws VolleyError {

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            T data = gson.fromJson(jsonReader, type);
            return FileResponse.success(data);
        } catch (FileNotFoundException e) {
            throw new VolleyError(e);
        }
    }

    @Override
    protected void deliverResult(T result) {
        listener.onResponse(result);
    }
}
