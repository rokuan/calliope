package apps.rokuan.com.calliope_helper.service;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public class ConnectionService extends Service {
    public static final int BLUETOOTH_CONNECTION = 0;
    public static final int INTERNET_CONNECTION = 1;
    public static final int JSON_MESSAGE = 2;
    //public static final int DISCONNECTION = 3;
    public static final int EXIT = 4;

    class ConnectionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BLUETOOTH_CONNECTION:
                    socket = new BluetoothDataSocket((BluetoothSocket)msg.obj);
                    Log.i("ConnectionService", "BluetoothDataSocket connected");
                    break;
                case INTERNET_CONNECTION:
                    socket = new WifiDataSocket((Socket)msg.obj);
                    Log.i("ConnectionService", "WifiDataSocket connected");
                    break;
                /*case DISCONNECTION:
                    break;*/
                case JSON_MESSAGE:
                    if(socket != null){
                        byte[] jsonData = ((String)msg.obj).getBytes();
                        byte[] jsonLength = new byte[4];

                        jsonLength[0] = (byte)jsonData.length;
                        jsonLength[1] = (byte)(jsonData.length >> 8);
                        jsonLength[2] = (byte)(jsonData.length >> 16);
                        jsonLength[3] = (byte)(jsonData.length >> 24);

                        try {
                            socket.write(jsonLength, 0, jsonLength.length);
                            socket.write(jsonData, 0, jsonData.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EXIT:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private DataSocket socket;
    private Messenger messenger = new Messenger(new ConnectionHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(socket != null){
            Log.i("ConnectionService", "closing socket...");
            socket.close();
            socket = null;
        }
    }
}
