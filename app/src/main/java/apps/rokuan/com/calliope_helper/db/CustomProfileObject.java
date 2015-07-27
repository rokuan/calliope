package apps.rokuan.com.calliope_helper.db;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.sentence.CustomObject;

/**
 * Created by LEBEAU Christophe on 21/07/15.
 */
@DatabaseTable(tableName = "custom_objects")
public class CustomProfileObject extends CustomObject implements ProfileRelated {
    @DatabaseField(columnName = Profile.PROFILE_COLUMN_NAME, foreign = true, index = true)
    private Profile profile;

    public CustomProfileObject(){

    }

    public CustomProfileObject(String value, String code){
        super(value, code);
    }

    @Override
    public void setProfile(Profile p){
        profile = p;
    }
}
