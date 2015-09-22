package apps.rokuan.com.calliope_helper.api;

import java.util.List;

import apps.rokuan.com.calliope_helper.db.ProfileVersion;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by LEBEAU Christophe on 14/09/2015.
 */
public interface CalliopeService {
    // Authentication

    // post('/login')
    // post('/logout')

    // Account

    @POST("/user")
    Call<OperationResult> createAccount(@Body User user);

    @PUT("/user/{id}")
    Call<OperationResult> updateAccountAvatar(/* TODO: */);

    @GET("/user/{id}")
    Call<User> getUser(@Path("id") String userId);

    @DELETE("/user/{id}")
    Call<OperationResult> deleteAccount(@Path("id") String userId);

    // Profiles

    @POST("/profile")
    Call<OperationResult> createProfile(String name, String description);

    @PUT("/profile/{id}")
    Call<OperationResult> updateProfile();

    @GET("/profile/{id}")
    Call<Profile> getProfile(@Path("id") String id);

    @DELETE("/profile/{id}")
    Call<OperationResult> deleteProfile(@Path("id") String id);

    @GET("/profile/")
    Call<List<Profile>> queryProfiles(@Query("q") String profileNameQuery);

    // Profile versions

    // TODO: faire en sorte que la valeur de retour soit un fichier
    @GET("/profile-version/{id}")
    Call<ProfileVersion> downloadProfileVersion(@Path("id") String profileVersionId);

    @DELETE("/profile-version/{id}")
    Call<OperationResult> deleteProfileVersion(@Path("id") String profileVersionId);
}
