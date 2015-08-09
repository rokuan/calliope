package apps.rokuan.com.calliope_helper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.fragment.PlaceHolderFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileDataFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileStoreFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfilesFragment;
import apps.rokuan.com.calliope_helper.fragment.SpeechFragment;
import apps.rokuan.com.calliope_helper.service.ConnectionService;


public class SpeechActivity extends ToolbarDrawerActivity {
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragmentToAdd = PlaceholderSpeechFragment.newInstance(position + 1);

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

    @Override
    public void onBackPressed(){
        if(isDrawerOpen()){
            closeDrawer();
        } else if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.exit_activity))
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.stopService(new Intent(this.getApplicationContext(), ConnectionService.class));
    }

    public static class PlaceholderSpeechFragment {
        public static PlaceHolderFragment newInstance(int sectionNumber){
            PlaceHolderFragment fragment;
            Bundle args = new Bundle();

            switch(sectionNumber){
                case 1:
                    fragment = new SpeechFragment();
                    break;
                case 2:
                    fragment = new ProfileStoreFragment();
                    break;
                case 3:
                    fragment = new ProfilesFragment();
                    break;
                case 4:
                /*case 5:
                case 6:
                case 7:*/
                default:
                    fragment = new ProfileDataFragment();
                    args.putBoolean(ProfileDataFragment.ARG_USE_ACTIVE_PROFILE, true);
                    //args.putInt(ProfileDataFragment.ARG_DATA_INITIAL_TAB, sectionNumber - 4);
                    break;
            }

            //Bundle args = new Bundle();
            args.putInt(PlaceHolderFragment.ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }
    }
}
