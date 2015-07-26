package com.crossbow.volley;

/**
 * Created by Patrick on 06/04/2015.
 */
public interface FileRequestFilter {

    /**
     *
     * @param fileRequest the request that is waiting
     * @return true to cancel the request, false otherwise
     */
    public boolean apply(FileRequest<?> fileRequest);
}
