package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;

/**
 * Created by LEBEAU Christophe on 19/07/15.
 */
public class ProfileDataFragment extends PlaceholderFragment {
    public static final int OBJECTS_TABS = 0;
    public static final int PLACES_TABS = 1;
    public static final int PEOPLE_TABS = 2;

    public static final String ARG_DATA_INITIAL_TAB = "initial_tab";

    private ViewPager mViewPager;
    private ProfileDataPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profile_data, parent, false);

        pagerAdapter = new ProfileDataPagerAdapter(this.getActivity().getSupportFragmentManager(), this.getActivity());

        mViewPager = (ViewPager) mainView.findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        //actionBar.setSelectedNavigationItem(position);
                    }
                }
        );
        mViewPager.setAdapter(pagerAdapter);

        Bundle args = this.getArguments();

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
                case OBJECTS_TABS:
                    fragment = new ObjectsFragment();
                    break;
                case PLACES_TABS:
                    fragment = new PlacesFragment();
                    break;
                case PEOPLE_TABS:
                default:
                    fragment = new PeopleFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
