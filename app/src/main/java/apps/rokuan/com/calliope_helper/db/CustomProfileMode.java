package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.fr.sentence.CustomMode;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
@DatabaseTable(tableName = "custom_modes")
public class CustomProfileMode extends CustomMode implements ProfileVersionRelated {
    @DatabaseField(columnName = ProfileVersion.VERSION_COLUMN_NAME, foreign = true, index = true)
    private ProfileVersion profileVersion;

    public CustomProfileMode(){

    }

    public CustomProfileMode(String value, String code){
        super(value, code);
    }

    @Override
    public void setProfileVersion(ProfileVersion pv) {
        profileVersion = pv;
    }
}
