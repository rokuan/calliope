package apps.rokuan.com.calliope_helper.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.fragment.ConnectionFragment;
import apps.rokuan.com.calliope_helper.fragment.PlaceholderFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileDataFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileStoreFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfilesFragment;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class ConnectionActivity extends NavigationDrawerActivity implements FragmentManager.OnBackStackChangedListener {
    @Override
    protected void onResume(){
        super.onResume();
        this.getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
        if(this.getSupportFragmentManager().getBackStackEntryCount() == 0){
            this.finish();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragmentToAdd = PlaceholderConnectionFragment.newInstance(position + 1);

        if(position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragmentToAdd)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .add(R.id.container, fragmentToAdd)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderConnectionFragment {
        public static PlaceholderFragment newInstance(int sectionNumber){
            PlaceholderFragment fragment;
            Bundle args = new Bundle();

            switch(sectionNumber){
                case 1:
                    fragment = new ConnectionFragment();
                    break;
                case 2:
                    fragment = new ProfileStoreFragment();
                    break;
                case 3:
                    fragment = new ProfilesFragment();
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                default:
                    args.putInt(ProfileDataFragment.ARG_DATA_INITIAL_TAB, sectionNumber - 4);
                    fragment = new ProfileDataFragment();
                    break;
            }

            //Bundle args = new Bundle();
            args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }
    }
}
