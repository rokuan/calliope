package apps.rokuan.com.calliope_helper.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelUuid;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Bind(R.id.bluetooth_disabled_frame) protected View disabledBluetoothFrame;
    @Bind(R.id.bluetooth_devices_list) protected ListView devicesListView;
    @Bind(R.id.bluetooth_scan) protected Button scanButton;

    private BluetoothDeviceAdapter deviceAdapter;
    private ProgressDialog bluetoothConnectionDialog;

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

    /*private final BroadcastReceiver deviceConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }
    };*/

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
        bluetoothAdapter.cancelDiscovery();
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
        closeProgressDialog();

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
        /*String deviceUuid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("Device UUID=" + deviceUuid);
        return UUID.fromString(deviceUuid);*/
        //ParcelUuid uuids = bluetoothAdapter.
        return MY_UUID_INSECURE;
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        BluetoothDevice device = deviceAdapter.getItem(position);

        try {
            new BluetoothSocketAsyncTask(device).execute(getDeviceUUID());
            //new BluetoothSocketAsyncTask().execute(device, UUID.randomUUID());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {

    }

    private void openProgressDialog(BluetoothDevice device){
        bluetoothConnectionDialog = ProgressDialog.show(this.getActivity(), device.getName(), this.getActivity().getString(R.string.connection), true);
    }

    private void closeProgressDialog(){
        if(bluetoothConnectionDialog != null){
            bluetoothConnectionDialog.dismiss();
            bluetoothConnectionDialog = null;
        }
    }

    class BluetoothSocketAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private BluetoothSocket s = null;
        private BluetoothDevice device;

        public BluetoothSocketAsyncTask(BluetoothDevice d){
            device = d;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            openProgressDialog(device);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            //BluetoothDevice device = (BluetoothDevice)params[0];
            // TODO: trouver le bon UUID
            //UUID uuid = (UUID)params[1];
            //UUID uuid = device.getUuids()[0].getUuid();
            /*UUID uuid = MY_UUID_INSECURE;

            try{
                s = device.createRfcommSocketToServiceRecord(uuid);
            }catch(Exception e2) {
                try {

                    s = device.createInsecureRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    //e.printStackTrace();
*/
                    try {
                        Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                        s = (BluetoothSocket) m.invoke(device, 1);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                        return false;
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                        return false;
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                        return false;
                    }
                /*}
            }*/

            try {
                s.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
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

            if(item.getBondState() == BluetoothDevice.BOND_BONDED){
                deviceAddress.setTextColor(Color.GREEN);
                deviceAddress.setText(this.getContext().getString(R.string.bonded_bluetooth_device));
            } else {
                deviceAddress.setTextColor(Color.GRAY);
                deviceAddress.setText(item.getAddress());
            }

            return v;
        }
    }
}
