package apps.rokuan.com.calliope_helper.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper.R;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class ConnectionFragment extends PlaceholderFragment {
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection, container, false);

        viewPager = (ViewPager)v.findViewById(R.id.pager);
        viewPager.setAdapter(new ConnectionPagerAdapter(this.getChildFragmentManager()));

        return v;
    }

    public class ConnectionPagerAdapter extends FragmentPagerAdapter {
        private final int[] tabsIcons = new int[]{ R.drawable.ic_bluetooth_white_36dp, R.drawable.ic_wifi_white_36dp };

        public ConnectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = getResources().getDrawable(tabsIcons[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

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
