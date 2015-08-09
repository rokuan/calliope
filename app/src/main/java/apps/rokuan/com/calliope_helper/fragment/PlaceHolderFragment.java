package apps.rokuan.com.calliope_helper.fragment;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */

import android.app.Activity;
import android.os.Bundle;

import apps.rokuan.com.calliope_helper.activity.ToolbarDrawerActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class PlaceHolderFragment extends CalliopeFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceHolderFragment newInstance(int sectionNumber) {
        PlaceHolderFragment fragment;

        switch(sectionNumber){
            case 0:
            default:
                fragment = new ConnectionFragment();
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public PlaceHolderFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            /*((NavigationDrawerActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));*/
            ((ToolbarDrawerActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }catch(Exception e){

        }
    }
}