package apps.rokuan.com.calliope_helper.fragment.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 01/08/15.
 */
public class ProfileEditFragment extends Fragment {
    @Bind(R.id.profile_edit_name) protected EditText profileNameView;
    @Bind(R.id.profile_edit_identifier) protected EditText profileIdentifierView;

    private CalliopeSQLiteOpenHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        this.setHasOptionsMenu(true);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_profile_save:
                saveProfile();
                return true;

            case R.id.action_profile_cancel:
                this.getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    private void saveProfile(){
        String profileName = profileNameView.getText().toString();
        String profileId = profileIdentifierView.getText().toString();

        if(profileName.isEmpty()){
            Toast.makeText(this.getActivity(), "Veuillez specifier un NOM", Toast.LENGTH_SHORT).show();
            return;
        }

        if(profileId.isEmpty()){
            Toast.makeText(this.getActivity(), "Veuillez specifier un IDENTIFIANT", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!profileId.matches("\\w+")){
            Toast.makeText(this.getActivity(), "L'IDENTIFIANT ne peut pas contenir de caracteres speciaux ou de ponctuation", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(profileId, profileName);

        if(db.addProfile(profile)){
            this.getActivity().getSupportFragmentManager().popBackStack();
        } else {
            // TODO: afficher un message d'erreur
            Log.e("ProfileEdit", "failed to save profile");
        }
    }
}
