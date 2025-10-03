package com.gpu.rentaler.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * 简化的Tar输入流实现
 * 无需外部依赖
 */
class TarInputStream extends InputStream {
    private final InputStream in;
    private TarEntry currentEntry;
    private long bytesRead;
    private long entrySize;

    public TarInputStream(InputStream in) {
        this.in = in;
    }

    public TarEntry getNextEntry() throws IOException {
        if (currentEntry != null) {
            // 跳过当前条目的剩余字节
            long remaining = entrySize - bytesRead;
            if (remaining > 0) {
                skip(remaining);
            }
            // 跳过填充字节（tar块对齐到512字节）
            long padding = (512 - (entrySize % 512)) % 512;
            if (padding > 0) {
                skip(padding);
            }
        }

        byte[] header = new byte[512];
        int totalRead = 0;
        while (totalRead < 512) {
            int read = in.read(header, totalRead, 512 - totalRead);
            if (read == -1) {
                return null;
            }
            totalRead += read;
        }

        // 检查是否为空块（文件结束标记）
        boolean allZero = true;
        for (byte b : header) {
            if (b != 0) {
                allZero = false;
                break;
            }
        }

        if (allZero) {
            return null;
        }

        currentEntry = new TarEntry(header);
        entrySize = currentEntry.getSize();
        bytesRead = 0;

        return currentEntry;
    }

    @Override
    public int read() throws IOException {
        if (bytesRead >= entrySize) {
            return -1;
        }
        int b = in.read();
        if (b != -1) {
            bytesRead++;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (bytesRead >= entrySize) {
            return -1;
        }

        long remaining = entrySize - bytesRead;
        int toRead = (int) Math.min(len, remaining);

        int read = in.read(b, off, toRead);
        if (read > 0) {
            bytesRead += read;
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = 0;
        byte[] buffer = new byte[4096];
        while (skipped < n) {
            int toRead = (int) Math.min(buffer.length, n - skipped);
            int read = read(buffer, 0, toRead);
            if (read == -1) {
                break;
            }
            skipped += read;
        }
        return skipped;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
