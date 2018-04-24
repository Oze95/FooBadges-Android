package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccessToken implements Serializable {

    @SerializedName("access_token")
    String access_token;

    @SerializedName("expires_in")
    String expires_in;

    @SerializedName("user_id")
    int user_id;

    @SerializedName("token_type")
    String token_type;

    @SerializedName("refresh_token")
    String refresh_token;
}