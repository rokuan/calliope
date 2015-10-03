package apps.rokuan.com.calliope_helper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.util.Insertable;
import com.rokuan.calliopecore.fr.sentence.CustomData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.ProfileVersionRelated;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public abstract class CustomDataFragment<T extends CustomData & ProfileVersionRelated> extends Fragment {
    private CalliopeSQLiteOpenHelper db;
    private CustomDataAdapter<T> adapter;
    private int profileVersionId;
    private Class<T> dataClass;
    private int dataDrawableId = R.drawable.ic_memory_black_48dp;

    @Bind(R.id.data_icon) protected ImageView dataIconView;
    @Bind(R.id.data_form_name) protected EditText dataNameView;
    @Bind(R.id.data_form_code) protected EditText dataCodeView;
    @Bind(R.id.fragment_data_list) protected DynamicListView dataListView;

    protected CustomDataFragment(Class<T> clazz, int drawableId){
        dataClass = clazz;
        dataDrawableId = drawableId;
    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_data_layout, parent, false);
        ButterKnife.bind(this, v);
        dataIconView.setImageResource(dataDrawableId);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        profileVersionId = this.getArguments().getInt(ProfileActivity.EXTRA_PROFILE_VERSION_KEY);

        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        adapter = new CustomDataAdapter<>(this.getActivity(), CalliopeSQLiteOpenHelper.queryProfileData(db, dataClass, profileVersionId, ""));
        this.setDataAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }

    @OnClick(R.id.data_form_save)
    public void saveData(){
        String dataName = dataNameView.getText().toString();
        String dataCode = dataCodeView.getText().toString();

        if(dataName.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ NOM obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        if(dataCode.isEmpty()){
            Toast.makeText(this.getActivity(), "Champ CODE obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Constructor<T> ctor = dataClass.getConstructor(String.class, String.class);
            T data = ctor.newInstance(dataName, dataCode);

            if(CalliopeSQLiteOpenHelper.addCustomData(db, dataClass, data, profileVersionId)){
                dataNameView.getText().clear();
                dataCodeView.getText().clear();
                dataListView.insert(0, data);
            } else {
                // TODO; afficher un message d'erreur
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final void setDataAdapter(BaseAdapter adapter){
        dataListView.disableDragAndDrop();
        dataListView.disableSwipeToDismiss();
        SwingLeftInAnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(adapter);
        animAdapter.setAbsListView(dataListView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(100);
        dataListView.setAdapter(animAdapter);
    }

    public static class CustomDataAdapter<T extends CustomData> extends ArrayAdapter<T> implements Insertable<T> {
        private LayoutInflater inflater;

        public CustomDataAdapter(Context context, List<T> objects) {
            super(context, R.layout.profile_data_list_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            T object = this.getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.profile_data_list_item, parent, false);
            }

            TextView objectValue = (TextView)v.findViewById(R.id.profile_data_item_name);
            TextView objectCode = (TextView)v.findViewById(R.id.profile_data_item_code);

            objectValue.setText(object.getValue());
            objectCode.setText(object.getCode());

            return v;
        }

        @Override
        public void add(int i, T object) {
            this.insert(object, 0);
        }
    }
}
