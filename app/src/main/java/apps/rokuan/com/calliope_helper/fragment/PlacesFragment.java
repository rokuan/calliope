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
import apps.rokuan.com.calliope_helper.db.CustomProfilePlace;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class PlacesFragment extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private ProfilePlaceAdapter adapter;
    private String profileId;

    @Bind(R.id.place_form_name) protected EditText placeNameView;
    @Bind(R.id.place_form_code) protected EditText placeCodeView;
    @Bind(R.id.fragment_data_places_list) protected ListView placesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_places, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.place_form_save)
    public void savePlace(){
        String placeName = placeNameView.getText().toString();
        String placeCode = placeCodeView.getText().toString();

        if(placeName.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ NOM obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        if(placeCode.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ CODE obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProfilePlace place = new CustomProfilePlace(placeName, placeCode);

        if(db.addCustomPlace(place, profileId)){
            // TODO: afficher un message de reussite
            placeNameView.getText().clear();
            placeCodeView.getText().clear();
            adapter.add(place);
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
        adapter = new ProfilePlaceAdapter(this.getActivity(), db.queryProfilePlaces(profileId, ""));
        placesList.setAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    class ProfilePlaceAdapter extends ArrayAdapter<CustomProfilePlace> {
        private LayoutInflater inflater;

        public ProfilePlaceAdapter(Context context, List<CustomProfilePlace> objects) {
            super(context, R.layout.profile_object_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            CustomProfilePlace place = this.getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.profile_object_item, parent, false);
            }

            TextView objectValue = (TextView)v.findViewById(R.id.profile_object_item_value);
            TextView objectCode = (TextView)v.findViewById(R.id.profile_object_item_code);

            objectValue.setText(place.getName());
            objectCode.setText(place.getCode());

            return v;
        }
    }
}
