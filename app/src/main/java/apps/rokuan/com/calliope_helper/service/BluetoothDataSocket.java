package apps.rokuan.com.calliope_helper.service;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public class BluetoothDataSocket extends DataSocket {
    private BluetoothSocket socket;

    public BluetoothDataSocket(BluetoothSocket s){
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
