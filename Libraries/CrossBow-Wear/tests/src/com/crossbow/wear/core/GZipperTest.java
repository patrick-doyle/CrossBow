package com.crossbow.wear.core;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

/**
 *
 */
public class GZipperTest {

    String text = "Bacon ipsum dolor amet esse turkey consequat in, andouille frankfurter adipisicing hamburger. Meatball tempor duis t-bone, " +
            "nostrud boudin shankle shoulder turkey occaecat tail culpa. Short ribs corned beef tempor hamburger reprehenderit culpa sunt." +
            " Id beef ribs jowl pork, non incididunt labore tongue ground round consequat eiusmod cupim lorem. Nisi irure venison bresaola sunt qui," +
            " quis ribeye ham sausage. In lorem boudin ribeye andouille. Cupidatat aliqua sint, duis porchetta shoulder fugiat non proident bacon " +
            "dolor biltong cillum ribeye chicken. Tenderloin frankfurter laboris eu swine aliqua salami officia commodo strip steak aute nostrud." +
            " Rump landjaeger adipisicing ea tongue pariatur picanha cow voluptate ullamco ad in boudin pig ipsum. Meatloaf in strip steak in shankle. " +
            "Sed nostrud jowl prosciutto eiusmod.";

    @Test
    public void testGzipper() throws IOException {
        byte[] compresed = com.crossbow.wear.core.Gzipper.zip(text.getBytes());

        byte[] decompressed = com.crossbow.wear.core.Gzipper.unzip(compresed);

        Assert.assertEquals(text, new String(decompressed));
    }
}
