package apps.rokuan.com.calliope_helper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class ProfileStoreFragment extends PlaceHolderFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_store, parent, false);
    }

    @Override
    public void refresh() {

    }
}
