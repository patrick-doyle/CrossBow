package com.crossbow.wear.core;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**

 */
public class WearNetworkResponseTest extends TestCase {

    @SmallTest
    public void testResponseSerialization() throws IOException {

        byte[] data = new byte[]{3,60,24,93,82,75,0x6};
        int statusCode = 340;
        long networkTime = 34065434;
        boolean modifed = true;
        boolean success = true;
        String uuid = UUID.randomUUID().toString();

        Map<String, String> headers = new HashMap<>();
        headers.put("Cache-Control", "max-age=3600,s-max-age=60,must-revalidate");
        headers.put("Etag", "vbsudbjsdcjsacdhvscdbs");

        WearNetworkResponse networkResponse = new WearNetworkResponse(success, data, uuid, 340, headers, modifed, networkTime);

        byte[] compressed = networkResponse.toByteArray();

        WearNetworkResponse recreated = WearNetworkResponse.fromByteArray(compressed);

        assertTrue(Arrays.equals(data, recreated.data));
        assertEquals(statusCode, recreated.statusCode);
        assertEquals(networkTime, recreated.networkTimeMs);
        assertTrue(recreated.notModified);
        assertTrue(recreated.success);
        assertEquals(uuid, recreated.uuid);
        assertEquals("max-age=3600,s-max-age=60,must-revalidate",recreated.headers.get("Cache-Control"));
        assertEquals("vbsudbjsdcjsacdhvscdbs",recreated.headers.get("Etag"));
    }
}
