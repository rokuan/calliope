package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.rokuan.calliopecore.sentence.CustomPerson;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
public class CustomProfilePerson extends CustomPerson implements ProfileRelated {
    @DatabaseField(columnName = Profile.PROFILE_COLUMN_NAME, foreign = true, index = true)
    private Profile profile;

    public CustomProfilePerson(){

    }

    public CustomProfilePerson(String name, String code){
        super(name, code);
    }

    @Override
    public void setProfile(Profile p) {
        profile = p;
    }
}
