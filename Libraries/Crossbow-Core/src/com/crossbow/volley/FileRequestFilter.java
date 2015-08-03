package com.crossbow.volley;

/**

 */
public interface FileRequestFilter {

    /**
     *
     * @param fileRequest the request that is waiting
     * @return true to cancel the request, false otherwise
     */
    public boolean apply(FileRequest<?> fileRequest);
}
