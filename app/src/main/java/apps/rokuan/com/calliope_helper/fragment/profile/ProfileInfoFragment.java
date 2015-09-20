package apps.rokuan.com.calliope_helper.fragment.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.activity.ProfileActivity;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.db.Profile;
import apps.rokuan.com.calliope_helper.event.ProfileEvent;
import apps.rokuan.com.calliope_helper.fragment.ProfileDataFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by LEBEAU Christophe on 01/08/15.
 */
public class ProfileInfoFragment extends Fragment {
    private String profileId;
    private Profile profile;
    private CalliopeSQLiteOpenHelper db;

    @Bind(R.id.profile_info_icon) protected ImageView profileImageView;
    @Bind(R.id.profile_info_name) protected TextView profileNameView;
    @Bind(R.id.profile_info_id) protected TextView profileIdView;
    @Bind(R.id.select_profile) protected Button selectButton;
    @Bind(R.id.delete_profile) protected Button deleteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_profile_info, parent, false);

        if(this.getArguments() != null) {
            Bundle args = this.getArguments();

            if (args.containsKey(ProfileActivity.EXTRA_PROFILE_KEY)) {
                profileId = args.getString(ProfileActivity.EXTRA_PROFILE_KEY);
            }
        }

        ButterKnife.bind(this, mainView);

        return mainView;
    }

    @OnClick(R.id.select_profile)
    public void selectProfile(){
        this.getActivity().getSharedPreferences(Profile.PROFILE_PREF_KEY, 0).edit()
                .putString(Profile.ACTIVE_PROFILE_KEY, profileId)
                .apply();
        selectButton.setEnabled(false);
        // TODO: notifier du changement de profil
        EventBus.getDefault().post(new ProfileEvent(profile));
    }

    @OnClick(R.id.see_profile)
    public void seeProfile(){
        ProfileDataFragment fragment = new ProfileDataFragment();
        fragment.setArguments(this.getArguments());

        this.getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.delete_profile)
    public void deleteProfile(){
        new AlertDialog.Builder(this.getActivity())
                .setMessage(this.getString(R.string.delete_profile_message))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(db.deleteProfile(profileId)){
                            /*getActivity().getSupportFragmentManager()
                                    .popBackStack();*/
                            getActivity().onBackPressed();
                        } else {
                            // TODO: afficher un message s'il y a une erreur
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new CalliopeSQLiteOpenHelper(this.getActivity());
        try {
            profile = db.getProfile(profileId);

            profileImageView.setImageBitmap(profile.getIcon());
            profileNameView.setText(profile.getName());
            profileIdView.setText(profile.getIdentifier());

            String currentProfileId = this.getActivity().getSharedPreferences(Profile.PROFILE_PREF_KEY, 0)
                    .getString(Profile.ACTIVE_PROFILE_KEY, null);

            if(profile.getIdentifier().equals(currentProfileId)){
                selectButton.setEnabled(false);
            }

            if(profile.getIdentifier().equals(Profile.DEFAULT_PROFILE_CODE)){
                // TODO: trouver un moyen pour les profils ne pouvant pas etre supprimes par l'utilisateur
                deleteButton.setVisibility(View.GONE);
            }
        } catch (SQLException e) {
            // TODO: afficher une erreur a l'ecran et quitter le fragment
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        db.close();
        db = null;
    }
}
