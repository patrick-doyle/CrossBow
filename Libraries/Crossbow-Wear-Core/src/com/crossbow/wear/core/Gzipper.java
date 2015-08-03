package com.crossbow.wear.core;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Util Class for zipping and unzipping data for sending it the watch
 */
public class Gzipper {

    private static final byte FLAG_COMPRESSED = 2;
    private static final byte FLAG_UNCOMPRESSED = 1;

    public static byte[] unzip(byte[] rawData) {
        //seperate the first byte into data and flag
        byte flag = rawData[0];

        if(flag == FLAG_COMPRESSED) {
            Inflater inflater = new Inflater();
            inflater.setInput(rawData, 1, rawData.length - 1);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(rawData.length - 1);
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = 0;
                try {
                    count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
            }
            return outputStream.toByteArray();
        }
        else {
            //data was compressed, strip the first flag byte off the data
            byte[] data = new byte[rawData.length - 1];
            System.arraycopy(rawData, 1, data, 0, data.length);
            return data;
        }

    }

    public static byte[] zip(byte[] data) {

        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length + 1);
        //write a placeholder byte for the flag
        baos.write(FLAG_UNCOMPRESSED);
        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int byteCount = deflater.deflate(buf);
            baos.write(buf, 0, byteCount);
        }
        deflater.end();
        byte[] compressed = baos.toByteArray();

        if(compressed.length < data.length) {
            //compressed is smaller, change the front flag
            compressed[0] = FLAG_COMPRESSED;
            return compressed;
        }
        else {
            //compression was no help add flag to data and return
            byte[] finalData = new byte[data.length + 1];
            finalData[0] = FLAG_COMPRESSED;
            System.arraycopy(data, 0, finalData, 1, data.length);
            return finalData;
        }
    }
}
