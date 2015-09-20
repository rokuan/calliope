package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.sentence.CustomObject;

/**
 * Created by LEBEAU Christophe on 21/07/15.
 */
@DatabaseTable(tableName = "custom_objects")
public class CustomProfileObject extends CustomObject implements ProfileVersionRelated {
    @DatabaseField(columnName = ProfileVersion.VERSION_COLUMN_NAME, foreign = true, index = true)
    private ProfileVersion profileVersion;

    public CustomProfileObject(){

    }

    public CustomProfileObject(String value, String code){
        super(value, code);
    }

    @Override
    public void setProfileVersion(ProfileVersion pv){
        profileVersion = pv;
    }
}
