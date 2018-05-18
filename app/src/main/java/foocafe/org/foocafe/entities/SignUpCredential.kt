package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class SignUpCredential(userID: String, eventID: String) : Serializable {
    @SerializedName("user_id")
    var userID: String
        internal set

    @SerializedName("event_id")
    var eventID: String
        internal set

    init {
        this.userID = userID
        this.eventID = eventID
    }
}