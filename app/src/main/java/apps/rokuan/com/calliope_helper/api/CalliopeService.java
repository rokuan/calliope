package apps.rokuan.com.calliope_helper.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public interface CalliopeService {
    @GET("/users/{id}")
    Call<User> getUser(@Path("id") String userId);


    @GET("/profiles/{id}")
    Call<Profile> getProfile(@Path("id") String profileId);

    @GET("/profiles/")
    List<User> queryProfilesByName(@Query("q") String profileNameQuery);

    @GET("/profiles/{id}/download")
    Call<Profile> downloadProfile(@Path("id") String profileId, @Query("lang") String languageCode);
}
