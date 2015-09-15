package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.util.Insertable;

import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.CustomProfileObject;
import apps.rokuan.com.calliope_helper.db.CustomProfilePlace;
import apps.rokuan.com.calliope_helper.db.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class ObjectsFragment extends CustomDataFragment<CustomProfileObject> {
    public ObjectsFragment(){
        super(CustomProfileObject.class, R.drawable.ic_memory_black_48dp);
    }
}
