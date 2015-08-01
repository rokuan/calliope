package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.add_profile)
    public void createProfile(){
        this.getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new ProfileEditFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new ProfileAdapter(this.getActivity(), db.queryProfiles());
        profilesList.setAdapter(adapter);
        profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Profile selectedProfile = adapter.getItem(position);
                ProfileInfoFragment fragment = new ProfileInfoFragment();
                Bundle args = new Bundle();

                args.putString(ProfileDataFragment.EXTRA_PROFILE_KEY, selectedProfile.getIdentifier());
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        try {
            profilesList.setItemChecked(adapter.getPosition(db.getActiveProfile()), true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
