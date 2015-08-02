package apps.rokuan.com.calliope_helper.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import apps.rokuan.com.calliope_helper.fragment.ProfileEditFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileInfoFragment;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
public class ProfileActivity extends AppCompatActivity {
    public static final String EXTRA_PROFILE_BUNDLE_KEY = "profile_activity_bundle";
    public static final String EXTRA_SECTION_KEY = "profile_activity_section";
    public static final String EXTRA_PROFILE_KEY = "profile_to_display";

    public static final int PROFILE_INFO_SECTION = 0;
    public static final int PROFILE_EDIT_SECTION = 1;

    @Bind(R.id.profile_edit_name) protected EditText profileNameView;
    @Bind(R.id.profile_edit_identifier) protected EditText profileIdentifierView;

    private CalliopeSQLiteOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                }
            }
        });

        navigateToFragment(this.getIntent().getIntExtra(EXTRA_SECTION_KEY, PROFILE_INFO_SECTION));
    }

    private void navigateToFragment(int section){
        Fragment fragment = null;

        switch(section){
            case PROFILE_INFO_SECTION:
                fragment = new ProfileInfoFragment();
                fragment.setArguments(this.getIntent().getBundleExtra(EXTRA_PROFILE_BUNDLE_KEY));
                break;

            case PROFILE_EDIT_SECTION:
            default:
                fragment = new ProfileEditFragment();
                break;
        }

        /*this.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();*/
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_profile_save:
                saveProfile();
                return true;

            case R.id.action_profile_cancel:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this);
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
            Toast.makeText(this, "Veuillez specifier un NOM", Toast.LENGTH_SHORT).show();
            return;
        }

        if(profileId.isEmpty()){
            Toast.makeText(this, "Veuillez specifier un IDENTIFIANT", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!profileId.matches("\\w+")){
            Toast.makeText(this, "L'IDENTIFIANT ne peut pas contenir de caracteres speciaux ou de ponctuation", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(profileId, profileName);

        if(db.addProfile(profile)){
            this.finish();
        } else {
            // TODO: afficher un message d'erreur
        }
    }*/
}
