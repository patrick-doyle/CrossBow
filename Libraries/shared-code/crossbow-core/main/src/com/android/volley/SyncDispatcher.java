package com.android.volley;

public class SyncDispatcher {

    private Cache cache;

    private Network network;

    public SyncDispatcher(Cache cache, Network network) {
        this.cache = cache;
        this.network = network;
    }

    public <T> SyncResponse<T> processRequest(Request<T> request) {
        try {
            request.addMarker("request-sync-start");
            request.addMarker("request-sync-thread [" + Thread.currentThread().getName() + "]");
            if (request.isCanceled() || request.hasHadResponseDelivered()) {
                return new SyncResponse<>(new VolleyError("request canceled or already handled"));
            }

            //Check the cache for an entry
            String cacheKey = request.getCacheKey();
            Cache.Entry cacheEntry = cache.get(cacheKey);

            if (cacheEntry == null || cacheEntry.isExpired() || cacheEntry.refreshNeeded()) {
                request.addMarker("request-sync-cache-miss");
                //cache miss or dead entry execute network to fill the cache
                Response<T> response = executeNetwork(request);
                request.addMarker("request-sync-http-done");

                if (!response.cacheEntry.isExpired() && request.shouldCache()) {
                    cache.put(cacheKey, response.cacheEntry);
                    request.addMarker("request-sync-cache-written");
                }
                request.logMarkers();
                return new SyncResponse<>(response.result);
            } else {
                request.addMarker("request-sync-cache-hit");
                //cache hit, parse the cached result
                Response<T> response = parseResponse(new NetworkResponse(cacheEntry.data, cacheEntry.responseHeaders), request);
                request.addMarker("request-sync-cache-delivered");
                request.logMarkers();
                return new SyncResponse<>(response.result);
            }
        } catch (VolleyError volleyError) {
            request.addMarker("request-sync-error " + volleyError.getMessage());
            request.logMarkers();
            return new SyncResponse<>(volleyError);
        } catch (Exception exception) {
            request.addMarker("request-sync-cache-delivered " + exception.getMessage());
            request.logMarkers();
            return new SyncResponse<>(new VolleyError(exception));
        }
    }

    private <T> Response<T> executeNetwork(Request<T> request) throws VolleyError {
        request.addMarker("request-sync-execute-network");
        NetworkResponse response = network.performRequest(request);
        return parseResponse(response, request);
    }

    private <T> Response<T> parseResponse(NetworkResponse networkResponse, Request<T> request) {
        return request.parseNetworkResponse(networkResponse);
    }
}
