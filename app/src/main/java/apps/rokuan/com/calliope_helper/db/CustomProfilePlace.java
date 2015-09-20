package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.sentence.CustomPlace;

/**
 * Created by LEBEAU Christophe on 21/07/15.
 */
@DatabaseTable(tableName = "custom_places")
public class CustomProfilePlace extends CustomPlace implements ProfileVersionRelated {
    @DatabaseField(columnName = ProfileVersion.VERSION_COLUMN_NAME, foreign = true, index = true)
    private ProfileVersion profileVersion;

    public CustomProfilePlace(){

    }

    public CustomProfilePlace(String name, String code){
        super(name, code);
    }

    @Override
    public void setProfileVersion(ProfileVersion pv){
        profileVersion = pv;
    }
}
