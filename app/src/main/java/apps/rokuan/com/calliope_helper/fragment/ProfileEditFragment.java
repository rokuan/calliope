package apps.rokuan.com.calliope_helper.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;

/**
 * Created by LEBEAU Christophe on 01/08/15.
 */
public class ProfileEditFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }
}
