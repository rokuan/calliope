package apps.rokuan.com.calliope_helper.db;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
@DatabaseTable(tableName = "profiles")
public class Profile {
    public static final String PROFILE_COLUMN_NAME = "profile_id";
    public static final String PROFILE_PREF_KEY = "profile";
    public static final String ACTIVE_PROFILE_KEY = "active_profile";

    @DatabaseField(id = true)
    private String profileId;
    @DatabaseField
    private String profileName;
    private Bitmap profileIcon;

    public Profile(){

    }

    public Profile(String code, String name){
        profileId = code;
        profileName = name;
    }
}
