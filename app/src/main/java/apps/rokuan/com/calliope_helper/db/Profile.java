package apps.rokuan.com.calliope_helper.db;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LEBEAU Christophe on 18/07/15.
 */
@DatabaseTable(tableName = "profiles")
public class Profile {
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
