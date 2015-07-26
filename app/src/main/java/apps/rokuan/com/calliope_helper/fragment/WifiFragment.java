package apps.rokuan.com.calliope_helper.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper.R;
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
        String address = addressView.getText().toString();

        if(address.isEmpty()){
            // TODO:
            return;
        }


    }

    private boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
}
