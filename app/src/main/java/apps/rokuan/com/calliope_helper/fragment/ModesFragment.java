package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.CustomProfileMode;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
public class ModesFragment extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private ProfileModeAdapter adapter;
    private String profileId;

    @Bind(R.id.mode_form_text) protected EditText modeValueView;
    @Bind(R.id.mode_form_code) protected EditText modeCodeView;
    @Bind(R.id.fragment_data_modes_list) protected ListView modesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_modes, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.mode_form_save)
    public void saveMode(){
        String modeValue = modeValueView.getText().toString();
        String modeCode = modeCodeView.getText().toString();

        if(modeValue.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ VALEUR obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        if(modeCode.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ CODE obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProfileMode mode = new CustomProfileMode(modeValue, modeCode);

        if(db.addCustomMode(mode, profileId)){
            // TODO: afficher un message de reussite
            modeValueView.getText().clear();
            modeCodeView.getText().clear();
            adapter.add(mode);
            adapter.notifyDataSetChanged();
        } else {
            // TODO; afficher un message d'erreur
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Bundle args = this.getArguments();

        if(args.getBoolean(ProfileDataFragment.ARG_USE_ACTIVE_PROFILE)){
            profileId = Profile.getCurrentProfileId(this.getActivity());
        } else {
            profileId = this.getArguments().getString(ProfileActivity.EXTRA_PROFILE_KEY);
        }

        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new ProfileModeAdapter(this.getActivity(), db.queryProfileModes(profileId, ""));
        modesList.setAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    class ProfileModeAdapter extends ArrayAdapter<CustomProfileMode> {
        private LayoutInflater inflater;

        public ProfileModeAdapter(Context context, List<CustomProfileMode> objects) {
            super(context, R.layout.profile_object_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            CustomProfileMode mode = this.getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.profile_object_item, parent, false);
            }

            TextView modeValue = (TextView)v.findViewById(R.id.profile_object_item_value);
            TextView modeCode = (TextView)v.findViewById(R.id.profile_object_item_code);

            modeValue.setText(mode.getContent());
            modeCode.setText(mode.getCode());

            return v;
        }
    }
}
