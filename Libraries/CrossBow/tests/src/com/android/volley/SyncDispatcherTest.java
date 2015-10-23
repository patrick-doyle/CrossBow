package com.android.volley;

import com.android.volley.mock.ShadowSystemClock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowSystemClock.class})
public class SyncDispatcherTest {

    @Mock
    private Network mockNetwork;

    @Mock
    private Cache cache;

    @Mock
    private Cache.Entry cacheEntry;

    @Mock
    private Request request;

    private SyncDispatcher dispatcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        dispatcher = new SyncDispatcher(cache, mockNetwork);
        Response response = Response.success(null, cacheEntry);

        when(cache.get(anyString())).thenReturn(cacheEntry);
        when(request.parseNetworkResponse(any(NetworkResponse.class))).thenReturn(response);
        when(request.getCacheKey()).thenReturn("test-cache-key");
    }

    @Test
    public void testCacheAccess() {
        dispatcher.processRequest(request);
        verify(cache).get("test-cache-key");
    }

    @Test
    public void testNetworkAccess() throws VolleyError {
        when(cacheEntry.isExpired()).thenReturn(true);
        dispatcher.processRequest(request);
        verify(mockNetwork).performRequest(request);
    }

    @Test
    public void testCachePut() throws VolleyError {
        //Given
        when(cacheEntry.isExpired()).thenReturn(true);

        //mock non expired entry for return
        Cache.Entry networkEntry = new Cache.Entry();
        networkEntry.ttl = System.currentTimeMillis() + 100000;

        NetworkResponse networkResponse = mock(NetworkResponse.class);
        when(mockNetwork.performRequest(any(Request.class))).thenReturn(networkResponse);

        Response response = Response.success(new Object(), networkEntry);
        when(request.parseNetworkResponse(any(NetworkResponse.class))).thenReturn(response);
        request.setShouldCache(true);

        //When
        dispatcher.processRequest(request);

        //Then
        verify(cache).put("test-cache-key", networkEntry);
    }

    @Test
    public void testRequestNoCache() throws VolleyError {
        when(cacheEntry.isExpired()).thenReturn(true);

        //mock non expired entry for return
        Cache.Entry networkEntry = mock(Cache.Entry.class);
        when(networkEntry.isExpired()).thenReturn(false);

        Response response = Response.success(null, networkEntry);
        when(request.parseNetworkResponse(any(NetworkResponse.class))).thenReturn(response);

        dispatcher.processRequest(request);
        verify(cache, never()).put("test-cache-key", networkEntry);
        verify(mockNetwork).performRequest(request);
    }

    @Test
    public void testCachePutExpired() throws VolleyError {
        when(cacheEntry.isExpired()).thenReturn(true);

        //mock non expired entry for return
        Cache.Entry networkEntry = mock(Cache.Entry.class);
        when(networkEntry.isExpired()).thenReturn(true);

        Response response = Response.success(null, networkEntry);
        when(request.parseNetworkResponse(any(NetworkResponse.class))).thenReturn(response);

        dispatcher.processRequest(request);
        verify(cache, never()).put("test-cache-key", networkEntry);
    }

    @Test
    public void testCacheEntryNull() throws VolleyError {
        when(cache.get(anyString())).thenReturn(null);

        dispatcher.processRequest(request);
        verify(mockNetwork).performRequest(request);
    }

    @Test
    public void testRefresh() throws VolleyError {
        when(cacheEntry.isExpired()).thenReturn(false);
        when(cacheEntry.refreshNeeded()).thenReturn(true);

        //mock non expired entry for return
        Cache.Entry networkEntry = mock(Cache.Entry.class);
        when(networkEntry.isExpired()).thenReturn(false);

        Response response = Response.success(null, networkEntry);
        when(request.parseNetworkResponse(any(NetworkResponse.class))).thenReturn(response);
        request.setShouldCache(true);

        dispatcher.processRequest(request);
        verify(cache).put("test-cache-key", networkEntry);
        verify(mockNetwork).performRequest(request);
    }

}
