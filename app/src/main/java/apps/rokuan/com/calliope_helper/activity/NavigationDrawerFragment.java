package apps.rokuan.com.calliope_helper.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import apps.rokuan.com.calliope_helper.event.ProfileEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String STATE_POSITIONS_HISTORY = "navigation_drawer_positions_history";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    public static final int HOME_SECTION = 0;
    public static final int STORE_SECTION = 1;
    public static final int PROFILES_SECTION = 2;
    public static final int OBJECTS_SECTION = 3;
    public static final int PLACES_SECTION = 4;
    public static final int PEOPLE_SECTION = 5;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    @Bind(R.id.menu_list) protected ListView mDrawerListView;
    @Bind(R.id.profile_icon) protected CircleImageView profileIconView;
    @Bind(R.id.profile_name) protected TextView profileNameView;
    @Bind(R.id.profile_code) protected TextView profileCodeView;

    private int mCurrentSelectedPosition = 0;
    private ArrayList<Integer> mPositionsHistory = new ArrayList<>();
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private EventBus mBus = EventBus.getDefault();
    private Profile mCurrentProfile;

    public NavigationDrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mPositionsHistory = savedInstanceState.getIntegerArrayList(STATE_POSITIONS_HISTORY);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);

        this.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() < mPositionsHistory.size()) {
                    mPositionsHistory.remove(mPositionsHistory.size() - 1);
                    try {
                        navigateToPosition(mPositionsHistory.get(mPositionsHistory.size() - 1));
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        ButterKnife.bind(this, mainView);

        //mDrawerListView = (ListView) mainView.findViewById(R.id.menu_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != mCurrentSelectedPosition) {
                    selectItem(position);
                }
            }
        });
        mDrawerListView.setAdapter(new MenuAdapter(getActionBar().getThemedContext()));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return mainView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        mPositionsHistory.add(position);

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        /*if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }*/
        close();
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private void navigateToPosition(int position){
        mCurrentSelectedPosition = position;

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
    }

    public void close(){
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        mBus.register(this);

        CalliopeSQLiteOpenHelper db = new CalliopeSQLiteOpenHelper(this.getActivity());
        try {
            Profile activeProfile = db.getActiveProfile();
            setCurrentProfile(activeProfile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
    }

    @Override
    public void onPause(){
        super.onPause();
        mBus.unregister(this);
    }

    public void onEvent(ProfileEvent event){
        setCurrentProfile(event.getProfile());
    }

    private void setCurrentProfile(Profile p){
        if(p != null) {
            mCurrentProfile = p;
            profileIconView.setImageBitmap(p.getIcon());
            profileNameView.setText(p.getName());
            profileCodeView.setText(p.getIdentifier());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        outState.putIntegerArrayList(STATE_POSITIONS_HISTORY, mPositionsHistory);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public class MenuAdapter extends ArrayAdapter<String> {
        private int[] drawables;
        private String[] names;
        private LayoutInflater inflater;

        public MenuAdapter(Context context) {
            super(context, R.layout.drawer_menu_item);

            TypedArray array = context.getResources().obtainTypedArray(R.array.sections_icons);
            drawables = new int[array.length()];

            for(int i=0; i<drawables.length; i++){
                drawables[i] = array.getResourceId(i, R.drawable.ic_shop_white_24dp);
            }

            names = context.getResources().getStringArray(R.array.sections);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount(){
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.drawer_menu_item, parent, false);
            }

            ImageView iconView = (ImageView)v.findViewById(R.id.menu_item_icon);
            TextView nameView = (TextView)v.findViewById(R.id.menu_item_name);

            iconView.setImageResource(drawables[position]);
            nameView.setText(names[position]);

            return v;
        }
    }
}
