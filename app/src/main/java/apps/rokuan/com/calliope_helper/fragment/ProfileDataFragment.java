package apps.rokuan.com.calliope_helper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.HomeActivity;
import apps.rokuan.com.calliope_helper.R;

/**
 * Created by LEBEAU Christophe on 19/07/15.
 */
public class ProfileDataFragment extends HomeActivity.PlaceholderFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profile_data, parent, false);

        return mainView;
    }
}
