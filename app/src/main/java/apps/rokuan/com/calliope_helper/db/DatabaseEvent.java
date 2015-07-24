package apps.rokuan.com.calliope_helper.db;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class DatabaseEvent {
    private String message;

    public DatabaseEvent(String msg){
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}
