package apps.rokuan.com.calliope_helper.api;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public class User {
    private String name;
    private String email;
    private String login;
    private String password;
    private Bitmap avatar;
    private List<Profile> profiles;

    public User(String userName, String userEmail, String userLogin, String userPassword){

    }

    public String getName(){
        return name;
    }

    public Bitmap getLogo(){
        return avatar;
    }

    public List<Profile> getProfiles(){
        return profiles;
    }
}
