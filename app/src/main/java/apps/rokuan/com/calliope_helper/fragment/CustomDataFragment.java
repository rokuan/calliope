package apps.rokuan.com.calliope_helper.fragment;

import android.support.v4.app.Fragment;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import apps.rokuan.com.calliope_helper.R;
import butterknife.Bind;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public abstract class CustomDataFragment extends Fragment {
    @Bind(R.id.fragment_data_list) protected DynamicListView dataListView;

    protected final DynamicListView getDataListView(){
        return dataListView;
    }

    protected final void setDataAdapter(BaseAdapter adapter){
        dataListView.disableDragAndDrop();
        dataListView.disableSwipeToDismiss();
        SwingLeftInAnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(adapter);
        animAdapter.setAbsListView(dataListView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(100);
        dataListView.setAdapter(animAdapter);
    }
}
