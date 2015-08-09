package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class ProfilesFragment extends PlaceHolderFragment {
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
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        i.putExtra(ProfileActivity.EXTRA_SECTION_KEY, ProfileActivity.PROFILE_EDIT_SECTION);
        startActivity(i);
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
                Bundle args = new Bundle();
                args.putString(ProfileActivity.EXTRA_PROFILE_KEY, selectedProfile.getIdentifier());

                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra(ProfileActivity.EXTRA_PROFILE_BUNDLE_KEY, args);
                i.putExtra(ProfileActivity.EXTRA_SECTION_KEY, ProfileActivity.PROFILE_INFO_SECTION);
                startActivity(i);
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

    @Override
    public void refresh() {

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
