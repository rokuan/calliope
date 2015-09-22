package apps.rokuan.com.calliope_helper.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.fragment.profile.ProfileEditFragment;
import apps.rokuan.com.calliope_helper.fragment.profile.ProfileInfoFragment;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
public class ProfileActivity extends AppCompatActivity {
    public static final String EXTRA_PROFILE_BUNDLE_KEY = "profile_activity_bundle";
    public static final String EXTRA_SECTION_KEY = "profile_activity_section";
    public static final String EXTRA_PROFILE_KEY = "profile_to_display";
    public static final String EXTRA_PROFILE_VERSION_KEY = "profile_version_to_display";

    public static final int PROFILE_INFO_SECTION = 0;
    public static final int PROFILE_EDIT_SECTION = 1;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

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
        Fragment fragment;

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

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
