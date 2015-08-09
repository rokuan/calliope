package apps.rokuan.com.calliope_helper.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import apps.rokuan.com.calliope_helper.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 08/08/15.
 */
public abstract class ToolbarDrawerActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {
    @Bind(R.id.toolbar) protected Toolbar toolbar;
    @Bind(R.id.drawer_layout) protected DrawerLayout drawer;

    private CharSequence mTitle;
    private DrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        ButterKnife.bind(this);

        this.setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawer, toolbar);
    }

    public void onSectionAttached(int number) {
        mTitle = getResources().getStringArray(R.array.sections)[number - 1];
    }

    @Override
    public void onBackPressed(){
        if(mNavigationDrawerFragment.isDrawerOpen()){
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isDrawerOpen(){
        return mNavigationDrawerFragment.isDrawerOpen();
    }

    protected void closeDrawer(){
        mNavigationDrawerFragment.close();
    }
}
