package foocafe.org.foocafe

import foocafe.org.foocafe.entities.AccessToken
import foocafe.org.foocafe.entities.BadgeList
import foocafe.org.foocafe.entities.CheckInCredential
import foocafe.org.foocafe.entities.CheckInList
import foocafe.org.foocafe.entities.EventList
import foocafe.org.foocafe.entities.RegistrationCredential
import foocafe.org.foocafe.entities.SignUpCredential
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface FooCafeAPI {

    @GET("{chapter}/events.json")
    fun loadEvents(@Path("chapter") chapterSlug: String): Call<EventList>

    @GET("/badges.json")
    fun loadBadges(): Call<BadgeList>

    @GET("/users/{userId}/badges.json")
    fun loadMyBadges(@Path("userId") userId: Int): Call<BadgeList>

    @POST("/{chapter}/events/{id}/check_in.json")
    fun checkIn(@Body checkInCredential: CheckInCredential,
                @Path("chapter") chapter: String,
                @Path("id") eventID: String): Call<CheckInCredential>

    @POST("/{chapter}/events/{id}/sign_up.json")
    fun signUp(@Body signUpCredential: SignUpCredential,
               @Path("chapter") chapter: String,
               @Path("id") eventID: String): Call<SignUpCredential>

    @POST("/users.json")
    fun register(@Body registrationCredential: RegistrationCredential): Call<RegistrationCredential>

    @GET("/{chapter}/events/{id}/check_ins.json")
    fun loadCheckIns(@Path("chapter") chapter: String,
                     @Path("id") eventID: String): Call<CheckInList>

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getAccessToken(
            @Field("code") code: String,
            @Field("grant_type") grantType: String,
            @Field("client_id") client_id: String,
            @Field("client_secret") client_secret: String,
            @Field("redirect_uri") redirect_uri: String): Call<AccessToken>

    companion object {
        val ENDPOINT = "http://foocafe.org"
    }
}