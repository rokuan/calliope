package apps.rokuan.com.calliope_helper.fragment.store;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.api.CalliopeStoreAPI;
import apps.rokuan.com.calliope_helper.api.Profile;
import apps.rokuan.com.calliope_helper.api.User;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.view.LozengeImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by LEBEAU Christophe on 18/09/2015.
 */
public class StoreAccountFragment extends Fragment {
    public static final String ARG_USER_ID = "arg_store_user_id";

    private CalliopeSQLiteOpenHelper db;
    private String userId;
    private User user;

    @Bind(R.id.store_account_name) protected TextView accountNameView;
    @Bind(R.id.store_account_logo) protected LozengeImageView accountLogoView;
    @Bind(R.id.store_account_profiles) protected GridView accountProfilesGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_store_account, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        userId = this.getArguments().getString(ARG_USER_ID);

        CalliopeStoreAPI.getInstance().getService().getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                // TODO: verifier le type de la reponse
                renderUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO:
            }
        });
    }

    private void renderUser(User user){
        accountNameView.setText(user.getName());
        accountLogoView.setImageBitmap(user.getLogo());

        if(user.getProfiles() != null){
            accountProfilesGrid.setAdapter(new ProfileAdapter(this.getActivity(), user.getProfiles()));
        }
    }

    class ProfileAdapter extends ArrayAdapter<Profile> {
        private LayoutInflater inflater;

        public ProfileAdapter(Context context, List<Profile> objects) {
            super(context, R.layout.view_profile_version, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.account_profile_item, parent, false);
            }

            Profile profile = this.getItem(position);

            ImageView profileIconView = (ImageView)v.findViewById(R.id.account_profile_item_icon);
            TextView profileNameView = (TextView)v.findViewById(R.id.account_profile_item_name);

            profileIconView.setImageBitmap(profile.getIcon());
            profileNameView.setText(profile.getName());

            return v;
        }
    }
}
