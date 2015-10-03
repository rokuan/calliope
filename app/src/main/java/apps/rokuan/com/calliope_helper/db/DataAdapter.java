package apps.rokuan.com.calliope_helper.db;

/**
 * Created by LEBEAU Christophe on 03/10/2015.
 */
public interface DataAdapter<D> {
    D transform(String s);
}
