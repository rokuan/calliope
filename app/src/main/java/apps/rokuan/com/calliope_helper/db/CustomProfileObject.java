package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.sentence.CustomObject;

/**
 * Created by LEBEAU Christophe on 21/07/15.
 */
@DatabaseTable(tableName = "custom_objects")
public class CustomProfileObject extends CustomObject {
    @DatabaseField(columnName = Profile.PROFILE_COLUMN_NAME, foreign = true)
    private Profile profile;
}
