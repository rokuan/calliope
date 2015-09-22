package apps.rokuan.com.calliope_helper.api;


import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by LEBEAU Christophe on 22/09/2015.
 */
public class CalliopeStoreAPI {
    private static final String API_BASE_URL = "http://192.168.0.17";

    private static CalliopeStoreAPI api;
    private CalliopeService service;
    private Retrofit retro = new Retrofit.Builder()
            .addConverter(User.class, GsonConverterFactory.create().get(User.class))
            .addConverter(Profile.class, GsonConverterFactory.create().get(Profile.class))
            .addConverter(ProfileVersion.class, GsonConverterFactory.create().get(ProfileVersion.class))
            .addConverter(OperationResult.class, GsonConverterFactory.create().get(OperationResult.class))
            .baseUrl(API_BASE_URL)
            .build();

    private CalliopeStoreAPI(){
        this.service = this.retro.create(CalliopeService.class);
    }

    public static CalliopeStoreAPI getInstance(){
        if(api == null){
            api = new CalliopeStoreAPI();
        }

        return api;
    }

    public CalliopeService getService(){
        return service;
    }
}
