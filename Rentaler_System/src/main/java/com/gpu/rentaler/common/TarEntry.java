package com.gpu.rentaler.common;

class TarEntry {
    private String name;
    private long size;

    public TarEntry(byte[] header) {
        this.name = parseString(header, 0, 100);
        this.size = parseOctal(header, 124, 12);
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    private String parseString(byte[] header, int offset, int length) {
        int end = offset;
        while (end < offset + length && header[end] != 0) {
            end++;
        }
        return new String(header, offset, end - offset);
    }

    private long parseOctal(byte[] header, int offset, int length) {
        String str = parseString(header, offset, length).trim();
        if (str.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(str, 8);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
