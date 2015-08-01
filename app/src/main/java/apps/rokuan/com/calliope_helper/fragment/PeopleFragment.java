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
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.CustomProfilePerson;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
public class PeopleFragment extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private ProfilePersonAdapter adapter;
    private String profileId;

    @Bind(R.id.person_form_name) protected EditText personNameView;
    @Bind(R.id.person_form_code) protected EditText personCodeView;
    @Bind(R.id.fragment_data_people_list) protected ListView peopleList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_people, parent, false);
        profileId = this.getArguments().getString(ProfileDataFragment.EXTRA_PROFILE_KEY);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.person_form_save)
    public void savePerson(){
        String personName = personNameView.getText().toString();
        String personCode = personCodeView.getText().toString();

        if(personName.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ NOM obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProfilePerson person = new CustomProfilePerson(personName, personCode);

        if(db.addCustomPerson(person, profileId)){
            // TODO: afficher un message de reussite
            personNameView.getText().clear();
            personCodeView.getText().clear();
            adapter.add(person);
            adapter.notifyDataSetChanged();
        } else {
            // TODO; afficher un message d'erreur
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new ProfilePersonAdapter(this.getActivity(), db.queryProfilePeople(profileId, ""));
        peopleList.setAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    class ProfilePersonAdapter extends ArrayAdapter<CustomProfilePerson> {
        private LayoutInflater inflater;

        public ProfilePersonAdapter(Context context, List<CustomProfilePerson> objects) {
            super(context, R.layout.profile_object_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            CustomProfilePerson person = this.getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.profile_object_item, parent, false);
            }

            TextView personName = (TextView)v.findViewById(R.id.profile_object_item_value);
            TextView personCode = (TextView)v.findViewById(R.id.profile_object_item_code);

            personName.setText(person.getName());
            personCode.setText(person.getCode());

            return v;
        }
    }
}
