package apps.rokuan.com.calliope_helper.api;

/**
 * Created by LEBEAU Christophe on 21/09/2015.
 */
public class OperationResult {
    private boolean success = false;
    private String message;

    public boolean getResult(){
        return success;
    }

    public String getMessage(){
        return message;
    }
}
