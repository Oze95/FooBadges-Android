package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccessToken implements Serializable {

    @SerializedName("access_token")
    public String access_token;

    @SerializedName("expires_in")
    public String expires_in;

    @SerializedName("user_id")
    public int user_id;

    @SerializedName("token_type")
    public String token_type;

    @SerializedName("refresh_token")
    String refresh_token;
}