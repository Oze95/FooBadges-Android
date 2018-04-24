package foocafe.org.foocafe;

import foocafe.org.foocafe.entities.AccessToken;
import foocafe.org.foocafe.entities.BadgeList;
import foocafe.org.foocafe.entities.CheckInCredential;
import foocafe.org.foocafe.entities.CheckInList;
import foocafe.org.foocafe.entities.EventList;
import foocafe.org.foocafe.entities.RegistrationCredential;
import foocafe.org.foocafe.entities.SignUpCredential;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface FooCafeAPI {
    String ENDPOINT = "http://foocafe.org";

    @GET("{chapter}/events.json")
    Call<EventList> loadEvents(@Path("chapter") String chapterSlug);

    @GET("/badges.json")
    Call<BadgeList> loadBadges();

    @GET("/users/{userId}/badges.json")
    Call<BadgeList> loadMyBadges(@Path("userId") int userId);

    @POST("/{chapter}/events/{id}/check_in.json")
    Call<CheckInCredential> checkIn(@Body CheckInCredential checkInCredential,
                                    @Path("chapter") String chapter,
                                    @Path("id") String eventID);

    @POST("/{chapter}/events/{id}/sign_up.json")
    Call<SignUpCredential> signUp(@Body SignUpCredential signUpCredential,
                                  @Path("chapter") String chapter,
                                  @Path("id") String eventID);

    @POST("/users.json")
    Call<RegistrationCredential> register(@Body RegistrationCredential registrationCredential);

    @GET("/{chapter}/events/{id}/check_ins.json")
    Call<CheckInList> loadCheckIns(@Path("chapter") String chapter,
                                   @Path("id") String eventID);

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AccessToken> getAccessToken(
            @Field("code") String code,
            @Field("grant_type") String grantType,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("redirect_uri") String redirect_uri);
}