package util;


public class RingBuffer {
    private final byte[] buffer;
    private final int capacity;
    private final int blockAvailability;

    private int available;
    private int idGet;
    private int idPut;

    public RingBuffer() {
        this(4096, 0);
    }

    public RingBuffer(int capacity) {
        this(capacity, 0);
    }

    public RingBuffer(int capacity, int minAvailable) {
        this.capacity = capacity;
        this.blockAvailability = minAvailable;
        buffer = new byte[this.capacity];
    }

    public synchronized void clear() {
        idGet = idPut = available = 0;
    }


    public synchronized int get() throws InterruptedException {
        if (available <= blockAvailability) {
            this.wait();
        }
        byte value = buffer[idGet];
        idGet = (idGet + 1) % capacity;
        available--;
        return value;
    }

    public int get(byte[] dst) throws InterruptedException {
        return get(dst, 0, dst.length);
    }

    public synchronized int get(byte[] dst, int off, int len) throws InterruptedException {
        if (available <= blockAvailability) {
            this.wait();
        }

        int limit = idGet < idPut ? idPut : capacity;
        int count = Math.min(limit - idGet, len);
        System.arraycopy(buffer, idGet, dst, off, count);
        idGet += count;

        if (idGet == capacity) {
            int count2 = Math.min(len - count, idPut);
            if (count2 > 0) {
                System.arraycopy(buffer, 0, dst, off + count, count2);
                idGet = count2;
                count += count2;
            } else {
                idGet = 0;
            }
        }
        available -= count;
        return count;
    }

    public synchronized void put(byte value) {
        buffer[idPut] = value;
        idPut = (idPut + 1) % capacity;
        if (available == capacity) {
            idGet++;
        } else {
            available++;
        }
        if (available > blockAvailability) {
            this.notify();
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
        } else {
            adjLen = len;
            adjOff = off;
        }

        int count = Math.min(capacity - idPut, adjLen);
        System.arraycopy(src, adjOff, buffer, idPut, count);
        idPut += count;

        if (idPut == capacity) {
            int count2 = adjLen - count;
            if (count2 > 0) {
                System.arraycopy(src, adjOff + count, buffer, 0, count2);
                idPut = count2;
                count += count2;
            } else {
                idPut = 0;
            }
        }
        available += count;

        if (available > capacity) {
            idGet = idPut;
            available = capacity;
        }

        if (available > blockAvailability) {
            this.notify();
        }
    }

    public synchronized int peek() {
        return available > 0 ? buffer[idGet] : -1;
    }

    public synchronized int skip(int count) {
        if (count > available) {
            count = available;
        }
        idGet = (idGet + count) % capacity;
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
