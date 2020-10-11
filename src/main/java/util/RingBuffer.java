package util;


public class RingBuffer {
    private final byte[] buffer;
    private final int capacity;

    private int available;
    private int idxGet;
    private int idxPut;

    public RingBuffer() {
        this(4096);
    }

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        buffer = new byte[this.capacity];
    }

    public synchronized void clear() {
        idxGet = idxPut = available = 0;
    }


    public synchronized int get() {
        if (available == 0) {
            return -1;
        }
        byte value = buffer[idxGet];
        idxGet = (idxGet + 1) % capacity;
        available--;
        return value;
    }

    public int get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public synchronized int get(byte[] dst, int off, int len) {
        if (available == 0) {
            return 0;
        }

        int limit = idxGet < idxPut ? idxPut : capacity;
        int count = Math.min(limit - idxGet, len);
        System.arraycopy(buffer, idxGet, dst, off, count);
        idxGet += count;

        if (idxGet == capacity) {
            int count2 = Math.min(len - count, idxPut);
            if (count2 > 0) {
                System.arraycopy(buffer, 0, dst, off + count, count2);
                idxGet = count2;
                count += count2;
            } else {
                idxGet = 0;
            }
        }
        available -= count;
        return count;
    }

    public synchronized void put(byte value) {
        buffer[idxPut] = value;
        idxPut = (idxPut + 1) % capacity;
        if (available == capacity) {
            idxGet++;
        } else {
            available++;
        }
    }

    public void put(byte[] src) {
        put(src, 0, src.length);
    }

    public synchronized void put(byte[] src, int off, int len) {
        int adjLen;
        int adjOff;
        if (len > capacity) {
            adjLen = capacity;
            adjOff = len - capacity + off;
        }
        else {
            adjLen = len;
            adjOff = off;
        }

        int count = Math.min(capacity - idxPut, adjLen);
        System.arraycopy(src, adjOff, buffer, idxPut, count);
        idxPut += count;

        if (idxPut == capacity) {
            int count2 = adjLen - count;
            if (count2 > 0) {
                System.arraycopy(src, adjOff + count, buffer, 0, count2);
                idxPut = count2;
                count += count2;
            } else {
                idxPut = 0;
            }
        }
        available += count;

        if (available > capacity) {
            idxGet = idxPut;
            available = capacity;
        }
    }

    public synchronized int peek() {
        return available > 0 ? buffer[idxGet] : -1;
    }

    public synchronized int skip(int count) {
        if (count > available) {
            count = available;
        }
        idxGet = (idxGet + count) % capacity;
        available -= count;
        return count;
    }

    public int capacity() {
        return capacity;
    }

    public synchronized int available() {
        return available;
    }

    public synchronized int free() {
        return capacity - available;
    }
}
