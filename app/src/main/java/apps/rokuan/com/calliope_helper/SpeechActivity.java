package apps.rokuan.com.calliope_helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import apps.rokuan.com.calliope_helper.fragment.ConnectionFragment;
import apps.rokuan.com.calliope_helper.fragment.PlaceholderFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileDataFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfileStoreFragment;
import apps.rokuan.com.calliope_helper.fragment.ProfilesFragment;
import apps.rokuan.com.calliope_helper.fragment.SpeechFragment;
import apps.rokuan.com.calliope_helper.service.ConnectionService;


public class SpeechActivity extends NavigationDrawerActivity {
    private static final String BACK_STACK_NAME = "speechBackStack";

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(position == 0) {
            fragmentManager.popBackStack();
        }

        fragmentManager.beginTransaction()
                .add(R.id.container, PlaceholderSpeechFragment.newInstance(position + 1))
                .addToBackStack(BACK_STACK_NAME)
                .commit();
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
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    protected void onResume(){
        super.onResume();
    }*/

    /*@Override
    protected void onPause(){
        super.onPause();
        this.unbindService(serviceConnection);
    }*/

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.stopService(new Intent(this.getApplicationContext(), ConnectionService.class));
    }

    public static class PlaceholderSpeechFragment {
        public static PlaceholderFragment newInstance(int sectionNumber){
            PlaceholderFragment fragment;
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
