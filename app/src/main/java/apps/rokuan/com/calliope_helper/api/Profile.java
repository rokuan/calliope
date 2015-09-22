package apps.rokuan.com.calliope_helper.api;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public class Profile {
    private String profileName;
    private String profileId;
    private Bitmap profileIcon;
    private String profileDescription;
    private List<ProfileVersion> versions;

    public String getName() {
        return profileName;
    }

    public String getId(){
        return profileId;
    }

    public String getDescription(){
        return profileDescription;
    }

    public Bitmap getIcon() {
        return profileIcon;
    }

    public List<ProfileVersion> getVersions() {
        return versions;
    }
}
