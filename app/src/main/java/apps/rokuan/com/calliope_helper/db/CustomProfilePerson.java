package apps.rokuan.com.calliope_helper.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rokuan.calliopecore.fr.sentence.CustomPerson;

/**
 * Created by LEBEAU Christophe on 29/07/15.
 */
@DatabaseTable(tableName = "custom_people")
public class CustomProfilePerson extends CustomPerson implements ProfileVersionRelated {
    @DatabaseField(columnName = ProfileVersion.VERSION_COLUMN_NAME, foreign = true, index = true)
    private ProfileVersion profileVersion;

    public CustomProfilePerson(){

    }

    public CustomProfilePerson(String name, String code){
        super(name, code);
    }

    @Override
    public void setProfileVersion(ProfileVersion pv) {
        profileVersion = pv;
    }
}
