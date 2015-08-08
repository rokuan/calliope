package apps.rokuan.com.calliope_helper.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.SpeechActivity;
import apps.rokuan.com.calliope_helper.service.ConnectionService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 26/07/15.
 */
public class BluetoothFragment extends CalliopeFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.bluetooth_disabled_frame) protected View disabledBluetoothFrame;
    @Bind(R.id.bluetooth_devices_list) protected ListView devicesListView;
    @Bind(R.id.bluetooth_scan) protected Button scanButton;

    private BluetoothDeviceAdapter deviceAdapter;

    private boolean bound = false;
    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message msg = Message.obtain(null, ConnectionService.BLUETOOTH_CONNECTION, socket);
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

    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        bluetoothAdapter.cancelDiscovery();
                        disabledBluetoothFrame.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        disabledBluetoothFrame.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver deviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceAdapter.add(device);
                deviceAdapter.notifyDataSetChanged();
            }
        }
    };

    private final BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                scanButton.setEnabled(true);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection_bluetooth, container, false);
        ButterKnife.bind(this, v);
        devicesListView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(isBluetoothEnabled()){
            disabledBluetoothFrame.setVisibility(View.INVISIBLE);
        }

        this.getActivity().registerReceiver(bluetoothState, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        this.getActivity().registerReceiver(deviceReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.getActivity().registerReceiver(bluetoothAdapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    public void onPause(){
        super.onPause();
        this.getActivity().unregisterReceiver(bluetoothState);
        this.getActivity().unregisterReceiver(deviceReceiver);
        this.getActivity().unregisterReceiver(bluetoothAdapterReceiver);
    }

    @OnClick(R.id.bluetooth_scan)
    public void scanForDevices(){
        scanButton.setEnabled(false);
        deviceAdapter = new BluetoothDeviceAdapter(this.getActivity(), new ArrayList<BluetoothDevice>());
        devicesListView.setAdapter(deviceAdapter);
        bluetoothAdapter.startDiscovery();
    }

    private void onTryConnect(boolean success, BluetoothSocket s){
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

    private UUID getDeviceUUID(){
        String deviceUuid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        return UUID.fromString(deviceUuid);
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = deviceAdapter.getItem(position);
        new BluetoothSocketAsyncTask().execute(device, getDeviceUUID());
    }

    @Override
    public void refresh() {

    }

    class BluetoothSocketAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private BluetoothSocket s = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            BluetoothDevice device = (BluetoothDevice)params[0];
            // TODO: trouver le bon UUID
            UUID uuid = (UUID)params[1];

            try {
                s = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            onTryConnect(result, s);
        }
    }

    class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        private LayoutInflater inflater;

        public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> objects) {
            super(context, R.layout.bluetooth_device_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.bluetooth_device_item, parent, false);
            }

            BluetoothDevice item = this.getItem(position);

            TextView deviceName = (TextView)v.findViewById(R.id.bluetooth_device_name);
            TextView deviceAddress = (TextView)v.findViewById(R.id.bluetooth_device_address);

            deviceName.setText(item.getName());
            deviceAddress.setText(item.getAddress());

            return v;
        }
    }
}
