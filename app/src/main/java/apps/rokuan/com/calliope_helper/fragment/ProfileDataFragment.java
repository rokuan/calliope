package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;

/**
 * Created by LEBEAU Christophe on 19/07/15.
 */
public class ProfileDataFragment extends PlaceholderFragment {
    public static final int OBJECTS_TAB = 0;
    public static final int PLACES_TAB = 1;
    public static final int PEOPLE_TAB = 2;
    public static final int MODES_TAB = 3;

    public static final String EXTRA_PROFILE_KEY = "profile_to_display";
    public static final String ARG_DATA_INITIAL_TAB = "initial_tab";

    private ViewPager mViewPager;
    private ProfileDataPagerAdapter pagerAdapter;

    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profile_data, parent, false);

        pagerAdapter = new ProfileDataPagerAdapter(this.getChildFragmentManager(), this.getActivity());
        mViewPager = (ViewPager) mainView.findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        Bundle args = this.getArguments();

        if(args.containsKey(EXTRA_PROFILE_KEY)){
            profileId = args.getString(EXTRA_PROFILE_KEY);
        } else {
            profileId = this.getActivity().getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                    .getString(Profile.ACTIVE_PROFILE_KEY, Profile.DEFAULT_PROFILE_CODE);
        }

        if(args.containsKey(ARG_DATA_INITIAL_TAB)){
            mViewPager.setCurrentItem(args.getInt(ARG_DATA_INITIAL_TAB));
        }

        return mainView;
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
            args.putString(EXTRA_PROFILE_KEY, profileId);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
