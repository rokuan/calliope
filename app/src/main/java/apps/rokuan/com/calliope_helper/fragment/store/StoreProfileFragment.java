package apps.rokuan.com.calliope_helper.fragment.store;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.api.LanguageVersion;
import apps.rokuan.com.calliope_helper.api.Profile;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.view.ProfileVersionView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 16/09/2015.
 */
public class StoreProfileFragment extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private Profile profile;

    @Bind(R.id.store_profile_name) protected TextView profileNameView;
    @Bind(R.id.store_profile_id) protected TextView profileIdView;
    @Bind(R.id.store_profile_description) protected TextView profileDescrView;
    @Bind(R.id.store_profile_versions) protected GridView profileVersionsGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_store_profile, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this.getActivity());

        // TODO: recuperer le profil a l'aide de l'API

        profileNameView.setText(profile.getName());
        profileIdView.setText(profile.getId());
        profileDescrView.setText(profile.getDescription());

        if(profile.getVersions() != null){
            profileVersionsGrid.setAdapter(new ProfileVersionAdapter(this.getActivity(), profile.getVersions()));
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if(db != null) {
            db.close();
            db = null;
        }
    }

    class ProfileVersionAdapter extends ArrayAdapter<LanguageVersion> {
        private List<ProfileVersionView> views = new ArrayList<>();

        public ProfileVersionAdapter(Context context, List<LanguageVersion> objects) {
            super(context, R.layout.view_profile_version, objects);

            for(LanguageVersion version: objects){
                views.add(new ProfileVersionView(context, profile, version));
            }
        }

        @Override
        public int getCount(){
            return views.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return views.get(position);
        }
    }
}
