package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class AccessToken : Serializable {

    @SerializedName("access_token")
    var access_token: String? = null

    @SerializedName("expires_in")
    var expires_in: String? = null

    @SerializedName("user_id")
    var user_id: Int = 0

    @SerializedName("token_type")
    var token_type: String? = null

    @SerializedName("refresh_token")
    internal var refresh_token: String? = null
}