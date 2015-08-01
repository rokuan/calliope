package apps.rokuan.com.calliope_helper.db;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
@DatabaseTable(tableName = "profiles")
public class Profile {
    public static final String DEFAULT_PROFILE_CODE = "default";

    public static final String PROFILE_COLUMN_NAME = "profile_id";
    public static final String PROFILE_PREF_KEY = "profile";
    public static final String ACTIVE_PROFILE_KEY = "active_profile";

    public static final String ID_FIELD_NAME = "profile_id";
    public static final String PROFILE_FIELD_NAME = "profile_name";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String profileId;
    @DatabaseField(columnName = PROFILE_FIELD_NAME)
    private String profileName;
    private Bitmap profileIcon;

    public Profile(){

    }

    public Profile(String code, String name){
        profileId = code;
        profileName = name;
    }

    public String getName(){
        return profileName;
    }

    public String getIdentifier(){
        return profileId;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof Profile) && this.profileId.equals(((Profile)o).getIdentifier());
    }
}
