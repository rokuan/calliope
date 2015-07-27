package apps.rokuan.com.calliope_helper.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public abstract class DataSocket {
    protected abstract OutputStream getOutputStream();

    public void write(int oneByte) throws IOException {
        OutputStream out = this.getOutputStream();
        out.write(oneByte);
        out.flush();
    }

    public void write(byte[] data) throws IOException {
        this.write(data, 0, data.length);
    }

    public void write(byte[] data, int offset, int length) throws IOException {
        OutputStream out = this.getOutputStream();
        out.write(data, offset, length);
        out.flush();
    }

    public abstract void close();
}
