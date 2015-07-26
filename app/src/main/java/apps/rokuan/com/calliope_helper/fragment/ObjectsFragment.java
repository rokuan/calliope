package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rokuan.calliopecore.sentence.CustomObject;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.CustomProfileObject;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class ObjectsFragment extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private ProfileObjectAdapter adapter;

    @Bind(R.id.object_form_text) protected EditText objectValueView;
    @Bind(R.id.object_form_code) protected EditText objectCodeView;
    @Bind(R.id.fragment_data_objects_list) protected ListView objectsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_objects, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.object_form_save)
    public void saveObject(){
        String objectValue = objectValueView.getText().toString();
        String objectCode = objectCodeView.getText().toString();

        if(objectValue.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ VALEUR obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        if(objectCode.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ CODE obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProfileObject object = new CustomProfileObject(objectValue, objectCode);

        if(db.addCustomObject(object)){
            // TODO: afficher un message de reussite
            objectValueView.getText().clear();
            objectCodeView.getText().clear();
            adapter.add(object);
            adapter.notifyDataSetChanged();
        } else {
            // TODO; afficher un message d'erreur
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new ProfileObjectAdapter(this.getActivity(), db.queryProfileObjects(""));
        objectsList.setAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    class ProfileObjectAdapter extends ArrayAdapter<CustomProfileObject> {
        private LayoutInflater inflater;

        public ProfileObjectAdapter(Context context, List<CustomProfileObject> objects) {
            super(context, R.layout.profile_object_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            CustomProfileObject object = this.getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.profile_object_item, parent, false);
            }

            TextView objectValue = (TextView)v.findViewById(R.id.profile_object_item_value);
            TextView objectCode = (TextView)v.findViewById(R.id.profile_object_item_code);

            objectValue.setText(object.getContent());
            objectCode.setText(object.getCode());

            return v;
        }
    }
}
