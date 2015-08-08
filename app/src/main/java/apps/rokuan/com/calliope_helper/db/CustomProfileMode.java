package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.sentence.CustomMode;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
@DatabaseTable(tableName = "custom_modes")
public class CustomProfileMode extends CustomMode implements ProfileRelated {
    @DatabaseField(columnName = Profile.PROFILE_COLUMN_NAME, foreign = true, index = true)
    private Profile profile;

    public CustomProfileMode(){

    }

    public CustomProfileMode(String value, String code){
        super(value, code);
    }

    @Override
    public void setProfile(Profile p) {
        profile = p;
    }
}
