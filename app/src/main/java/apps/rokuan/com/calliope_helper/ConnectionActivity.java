package apps.rokuan.com.calliope_helper;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import apps.rokuan.com.calliope_helper.fragment.ConnectionFragment;
import apps.rokuan.com.calliope_helper.fragment.PlaceholderFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileDataFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileStoreFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfilesFragment;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class ConnectionActivity extends NavigationDrawerActivity {
    private static final String BACK_STACK_NAME = "connectionBackStack";
    private int currentSelectedPosition = 0;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(position == 0) {
            fragmentManager.popBackStack();
        }

        if(position >= NavigationDrawerFragment.OBJECTS_SECTION && position == currentSelectedPosition){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderConnectionFragment.newInstance(position + 1))
                    .addToBackStack(BACK_STACK_NAME)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .add(R.id.container, PlaceholderConnectionFragment.newInstance(position + 1))
                    .addToBackStack(BACK_STACK_NAME)
                    .commit();
        }

        currentSelectedPosition = position;
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
