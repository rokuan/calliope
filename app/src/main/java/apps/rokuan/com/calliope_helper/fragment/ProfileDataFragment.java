package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import apps.rokuan.com.calliope_helper.db.ProfileVersion;

/**
 * Created by LEBEAU Christophe on 19/07/15.
 */
public class ProfileDataFragment extends PlaceHolderFragment {
    public static final int OBJECTS_TAB = 0;
    public static final int PLACES_TAB = 1;
    public static final int PEOPLE_TAB = 2;
    public static final int MODES_TAB = 3;

    public static final String ARG_USE_ACTIVE_PROFILE = "use_active_profile";

    private ViewPager mViewPager;
    private ProfileDataPagerAdapter pagerAdapter;
    private Spinner languageSpinner;
    private LanguageAdapter languageAdapter;

    private String profileId;
    private int profileVersionId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profile_data, parent, false);
        mViewPager = (ViewPager) mainView.findViewById(R.id.pager);
        this.setHasOptionsMenu(true);
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_data, menu);

        languageSpinner = (Spinner)MenuItemCompat.getActionView(menu.findItem(R.id.action_language_select));

        CalliopeSQLiteOpenHelper db = new CalliopeSQLiteOpenHelper(this.getActivity());
        List<ProfileVersion> availableLanguages = db.getAvailableProfileVersions(profileId);

        languageAdapter = new LanguageAdapter(this.getActivity(), availableLanguages);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        db.close();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_language_new:
                // TODO: acceder au fragment de creation d'une version
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        Bundle args = this.getArguments();

        if(args.getBoolean(ARG_USE_ACTIVE_PROFILE)){
            profileId = Profile.getCurrentProfileId(this.getActivity());
        } else {
            profileId = args.getString(ProfileActivity.EXTRA_PROFILE_KEY);
        }

        this.refresh();
    }

    @Override
    public void refresh() {
        if(profileVersionId >= 0) {
            pagerAdapter = new ProfileDataPagerAdapter(this.getChildFragmentManager(), this.getActivity());
            mViewPager.setAdapter(pagerAdapter);
        }
    }

    class ProfileDataPagerAdapter extends FragmentPagerAdapter {
        private String[] titles;

        public ProfileDataPagerAdapter(FragmentManager fm, Context context){
            super(fm);
            titles = context.getResources().getStringArray(R.array.profile_data_sections);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch(position){
                case OBJECTS_TAB:
                    fragment = new ObjectsFragment();
                    break;
                case PLACES_TAB:
                    fragment = new PlacesFragment();
                    break;
                case PEOPLE_TAB:
                    fragment = new PeopleFragment();
                    break;
                case MODES_TAB:
                default:
                    fragment = new ModesFragment();
                    break;
            }

            Bundle args = new Bundle();
            args.putBoolean(ARG_USE_ACTIVE_PROFILE, getArguments().getBoolean(ARG_USE_ACTIVE_PROFILE));
            args.putInt(ProfileActivity.EXTRA_PROFILE_VERSION_KEY, profileVersionId);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    class LanguageAdapter extends ArrayAdapter<ProfileVersion> {
        private LayoutInflater inflater;

        public LanguageAdapter(Context context, List<ProfileVersion> objects) {
            super(context, R.layout.language_version_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.language_version_item, parent, false);
            }

            ProfileVersion version = this.getItem(position);

            TextView versionCodeView = (TextView)v.findViewById(R.id.language_version_item_code);
            ImageView versionFlagView = (ImageView)v.findViewById(R.id.language_version_item_flag);

            versionCodeView.setText(version.getLanguage());

            int flagId = this.getContext().getResources().getIdentifier("flag_" + version.getLanguage(),
                    "drawable",
                    this.getContext().getPackageName());
            Picasso.with(this.getContext()).load(flagId)
                    .error(R.drawable.ic_file_download_black_24dp)  // TODO: remplacer par le drapeau universel
                    .into(versionFlagView);

            return v;
        }
    }
}
