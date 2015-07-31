package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class ProfilesFragment extends PlaceholderFragment {
    @Bind(R.id.fragment_profiles_list) protected ListView profilesList;

    private CalliopeSQLiteOpenHelper db;
    private ProfileAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profiles, parent, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new ProfileAdapter(this.getActivity(), db.queryProfiles());
        profilesList.setAdapter(adapter);
        profilesList.setItemChecked(0, true);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    class ProfileAdapter extends ArrayAdapter<Profile> {
        private LayoutInflater inflater;

        public ProfileAdapter(Context context, List<Profile> objects) {
            super(context, R.layout.profile_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.profile_item, parent, false);
            }

            Profile profile = this.getItem(position);

            TextView profileName = (TextView)v.findViewById(R.id.profile_item_name);
            TextView profileCode = (TextView)v.findViewById(R.id.profile_item_code);

            profileName.setText(profile.getName());
            profileCode.setText(profile.getIdentifier());

            return v;
        }
    }
}
