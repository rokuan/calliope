package apps.rokuan.com.calliope_helper.db;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LEBEAU Christophe on 20/09/2015.
 */
@DatabaseTable(tableName = "profile_versions")
public class ProfileVersion {
    public static final String VERSION_COLUMN_NAME = "version_id";
    public static final String UNIVERSAL_LANGUAGE_CODE = "uni";

    public static final String ID_FIELD_NAME = "version_id";
    public static final String LANGUAGE_FIELD_NAME = "language_code";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private int id;
    @DatabaseField(columnName = LANGUAGE_FIELD_NAME, uniqueCombo = true)
    private String language;
    @DatabaseField(columnName = Profile.PROFILE_COLUMN_NAME, foreign = true, index = true, uniqueCombo = true)
    private Profile profile;

    public ProfileVersion(){

    }

    public ProfileVersion(String languageCode){
        language = languageCode;
    }

    public int getId(){
        return id;
    }

    public String getLanguage(){
        return language;
    }

    public void setProfile(Profile p){
        profile = p;
    }
}
