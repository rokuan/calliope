package apps.rokuan.com.calliope_helper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Spinner;

import java.sql.SQLException;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 20/09/2015.
 */
public class ProfileVersionActivity extends AppCompatActivity {
    private String profileId;
    private CalliopeSQLiteOpenHelper db;
    private Profile profile;

    @Bind(R.id.activity_profile_version_spinner) protected Spinner newItemSpinner;
    @Bind(R.id.activity_profile_version_list) protected ListView availableItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_version);

        ButterKnife.bind(this);

        profileId = this.getIntent().getStringExtra(ProfileActivity.EXTRA_PROFILE_BUNDLE_KEY);
    }

    @OnClick(R.id.activity_profile_version_add)
    public void addNewProfileVersion(){
        // TODO:
    }

    @Override
    protected void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this);

        try {
            profile = db.getProfile(profileId);

            // TODO: completer l'interface a partir du profil
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: quitter l'activite en cas d'erreur
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(db != null){
            db.close();
        }
    }
}
