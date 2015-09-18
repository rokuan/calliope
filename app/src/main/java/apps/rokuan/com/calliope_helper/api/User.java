package apps.rokuan.com.calliope_helper.api;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public class User {
    private String userName;
    private Bitmap userImage;
    private List<Profile> userProfiles;

    public String getName(){
        return userName;
    }

    public Bitmap getLogo(){
        return userImage;
    }

    public List<Profile> getProfiles(){
        return userProfiles;
    }
}
