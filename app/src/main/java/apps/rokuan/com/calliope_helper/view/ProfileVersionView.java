package apps.rokuan.com.calliope_helper.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.api.CalliopeStoreAPI;
import apps.rokuan.com.calliope_helper.api.ProfileVersion;
import apps.rokuan.com.calliope_helper.api.Profile;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by LEBEAU Christophe on 17/09/2015.
 */
public class ProfileVersionView extends LinearLayout implements View.OnClickListener {
    private ProfileVersion version;

    @Bind(R.id.view_profile_version_flag) protected ImageView flagView;

    public ProfileVersionView(Context context, ProfileVersion lang) {
        super(context);
        version = lang;

        initProfileVersionView();
    }

    private void initProfileVersionView(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.view_profile_version, this, true);

        ButterKnife.bind(this);

        int id = this.getContext().getResources().getIdentifier("flag_" + version.getCountryCode().toLowerCase(),
                "drawable",
                this.getContext().getPackageName());

        if(id == 0){
            id = R.drawable.ic_person_black_48dp;
        }

        flagView.setImageResource(id);

        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(this.getContext())
                .setMessage(R.string.confirm_download)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        downloadVersion();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void downloadVersion(){
        CalliopeStoreAPI.getInstance().getService().downloadProfileVersion(version.getId()).enqueue(new Callback<apps.rokuan.com.calliope_helper.db.ProfileVersion>() {
            @Override
            public void onResponse(Response<apps.rokuan.com.calliope_helper.db.ProfileVersion> response) {
                Toast.makeText(ProfileVersionView.this.getContext(), version.getCountryCode() + "...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
