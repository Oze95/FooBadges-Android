package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class CheckInCredential(userID: Int, beaconID: String, deviceID: String, timestamp: String) : Serializable {

    @SerializedName("user_id")
    var userID: Int = 0
        internal set

    @SerializedName("beacon")
    var beaconID: String
        internal set

    @SerializedName("device")
    var deviceID: String
        internal set

    @SerializedName("recorded_at")
    var timestamp: String
        internal set

    init {
        this.userID = userID
        this.beaconID = beaconID
        this.deviceID = deviceID
        this.timestamp = timestamp
    }
}
