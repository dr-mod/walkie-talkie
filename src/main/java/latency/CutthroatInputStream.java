package latency;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CutthroatInputStream extends FilterInputStream {

    private final int maxAvailable;

    public CutthroatInputStream(InputStream in, int maxAvailable) {
        super(in);
        this.maxAvailable = maxAvailable;
    }

    @Override
    public int read() throws IOException {
        removeStaleData();
        return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        removeStaleData();
        return super.read(b, off, len);
    }

    private void removeStaleData() throws IOException {
        int available = this.available();
        if (available > maxAvailable) {
            this.skip(available - maxAvailable);
            System.out.println("Old available " + available + ", new available " + this.available());
        }
    }
}
