package apps.rokuan.com.calliope_helper.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.SpeechActivity;
import apps.rokuan.com.calliope_helper.service.ConnectionService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 26/07/15.
 */
public class WifiFragment extends Fragment {
    @Bind(R.id.wifi_disabled_frame) protected View disabledWifiFrame;
    @Bind(R.id.wifi_address) protected EditText addressView;
    @Bind(R.id.wifi_port) protected EditText portView;
    @Bind(R.id.wifi_password) protected EditText passwordView;

    private boolean bound = false;
    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message msg = Message.obtain(null, ConnectionService.INTERNET_CONNECTION, socket);
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            bound = true;
            unbindServiceAndStartActivity();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            bound = false;
        }
    };
    private Socket socket;

    class SocketAsyncTask extends AsyncTask<String, Void, Boolean> {
        private Socket s = null;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                s = new Socket(params[0], Integer.parseInt(params[1]));
            } catch (IOException e) {
                s = null;
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            onTryConnect(result, s);
        }
    }

    private BroadcastReceiver wifiState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                switch(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)){
                    case WifiManager.WIFI_STATE_ENABLED:
                        disabledWifiFrame.setVisibility(View.INVISIBLE);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                    default:
                        disabledWifiFrame.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection_wifi, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(isWifiEnabled()){
            disabledWifiFrame.setVisibility(View.INVISIBLE);
        }

        this.getActivity().registerReceiver(wifiState, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    public void onPause(){
        super.onPause();
        this.getActivity().unregisterReceiver(wifiState);
    }

    @OnClick(R.id.wifi_connect)
    public void connect(){
        String addressText = addressView.getText().toString();
        String portText = portView.getText().toString();

        if(addressText.isEmpty()){
            // TODO:
            return;
        }

        if(portText.isEmpty()){
            // TODO:
            return;
        }

        new SocketAsyncTask().execute(addressText, portText);
    }

    private void onTryConnect(boolean success, Socket s){
        if(success) {
            socket = s;
            startAndBindService();
        } else {
            // TODO: afficher l'erreur a l'ecran
            Toast.makeText(this.getActivity(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAndBindService(){
        Intent serviceIntent = new Intent(this.getActivity().getApplicationContext(), ConnectionService.class);
        this.getActivity().startService(serviceIntent);
        this.getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindServiceAndStartActivity(){
        this.getActivity().unbindService(serviceConnection);
        Intent i = new Intent(this.getActivity(), SpeechActivity.class);
        this.startActivity(i);
    }

    private boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
}
