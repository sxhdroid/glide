package com.bumptech.glide.load.data;

import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.bumptech.glide.util.ByteArrayPool;

import java.io.IOException;
import java.io.InputStream;

public final class InputStreamRewinder implements DataRewinder<InputStream> {
    public static class Factory implements DataRewinder.Factory<InputStream> {

        @Override
        public DataRewinder<InputStream> build(InputStream data) {
            return new InputStreamRewinder(data);
        }

        @Override
        public Class getDataClass() {
            return InputStream.class;
        }
    }

    // 5mb.
    private static final int MARK_LIMIT = 5 * 1024 * 1024;

    private final RecyclableBufferedInputStream bufferedStream;
    private final byte[] buffer;

    InputStreamRewinder(InputStream is) {
        ByteArrayPool byteArrayPool = ByteArrayPool.get();
        buffer = byteArrayPool.getBytes();
        bufferedStream = new RecyclableBufferedInputStream(is, buffer);
        bufferedStream.mark(MARK_LIMIT);
    }

    @Override
    public InputStream rewindAndGet() throws IOException {
        bufferedStream.reset();
        return bufferedStream;
    }

    @Override
    public void cleanup() {
        ByteArrayPool.get().releaseBytes(buffer);
    }
}
