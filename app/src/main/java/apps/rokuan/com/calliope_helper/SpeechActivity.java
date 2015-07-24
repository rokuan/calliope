package apps.rokuan.com.calliope_helper;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import apps.rokuan.com.calliope_helper.fragment.PlaceholderFragment;
import apps.rokuan.com.calliope_helper.fragment.SpeechFragment;


public class SpeechActivity extends NavigationDrawerActivity {
    private static final String BACK_STACK_NAME = "speechBackStack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(position == 0){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderSpeechFragment.newInstance(position + 1))
                    .addToBackStack(BACK_STACK_NAME)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderSpeechFragment.newInstance(position + 1))
                    .addToBackStack(BACK_STACK_NAME)
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

    public static class PlaceholderSpeechFragment {
        public static PlaceholderFragment newInstance(int sectionNumber){
            PlaceholderFragment fragment;

            switch(sectionNumber){
                case 0:
                default:
                    fragment = new SpeechFragment();
                    break;
            }

            Bundle args = new Bundle();
            args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }
    }
}
