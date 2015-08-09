package apps.rokuan.com.calliope_helper.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class ConnectionFragment extends PlaceHolderFragment {
    @Bind(R.id.tab_layout) protected TabLayout tabLayout;
    @Bind(R.id.pager) protected ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection, container, false);

        ButterKnife.bind(this, v);

        TabLayout.Tab bluetoothTab = tabLayout.newTab()
                .setIcon(R.drawable.bluetooth_tab_icon);
        TabLayout.Tab wifiTab = tabLayout.newTab()
                .setIcon(R.drawable.wifi_tab_icon);

        tabLayout.addTab(bluetoothTab);
        tabLayout.addTab(wifiTab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAdapter(new ConnectionPagerAdapter(this.getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.connection_section);
    }

    @Override
    public void refresh() {

    }

    public class ConnectionPagerAdapter extends FragmentPagerAdapter {
        //private final int[] tabsIcons = new int[]{ R.drawable.ic_bluetooth_white_36dp, R.drawable.ic_wifi_white_36dp };

        public ConnectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        /*@Override
        public CharSequence getPageTitle(int position) {
            Drawable image = getResources().getDrawable(tabsIcons[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }*/

        @Override
        public Fragment getItem(int position){
            Fragment fragment;

            switch(position){
                case 0:
                    fragment = new BluetoothFragment();
                    break;
                case 1:
                default:
                    fragment = new WifiFragment();
                    break;
            }

            return fragment;
        }
    }
}
