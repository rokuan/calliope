package apps.rokuan.com.calliope_helper.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public class WifiDataSocket extends DataSocket {
    private Socket socket;

    public WifiDataSocket(Socket s){
        socket = s;
    }

    @Override
    protected OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
