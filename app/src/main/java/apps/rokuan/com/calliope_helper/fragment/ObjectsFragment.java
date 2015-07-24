package apps.rokuan.com.calliope_helper.fragment;

import android.support.v7.app.AppCompatActivity;

import apps.rokuan.com.calliope_helper.R;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class ObjectsFragment extends PlaceholderFragment {
    @Override
    public void onResume(){
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.data_section);
    }
}
