package github.ch.compress.gzip;

import github.ch.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description:
 * @Author: 陈恒
 * @Time: 2021/2/25 下午11:08
 */
public class GzipCompress implements Compress {
    private final static int BUFFER_SIZE = 4 * 1024 * 1024;

    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipos = new GZIPOutputStream(baos)) {
            gzipos.write(bytes);
            gzipos.flush();
            gzipos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("gzip compress failed");
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gzipis = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = gzipis.read(buffer)) > 0)
                baos.write(buffer, 0, len);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("gzip decompress failed");
        }
    }
}
