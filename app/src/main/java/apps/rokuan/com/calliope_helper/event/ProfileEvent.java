package apps.rokuan.com.calliope_helper.event;

import apps.rokuan.com.calliope_helper.db.Profile;

/**
 * Created by LEBEAU Christophe on 02/08/15.
 */
public class ProfileEvent {
    private Profile profile;

    public ProfileEvent(Profile newProfile){
        profile = newProfile;
    }

    public Profile getProfile(){
        return profile;
    }
}
