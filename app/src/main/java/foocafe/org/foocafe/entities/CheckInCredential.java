package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckInCredential implements Serializable {

    @SerializedName("user_id")
    int userID;

    @SerializedName("beacon")
    String beaconID;

    @SerializedName("device")
    String deviceID;

    @SerializedName("recorded_at")
    String timestamp;

    public CheckInCredential(int userID, String beaconID, String deviceID, String timestamp){
        this.userID = userID;
        this.beaconID = beaconID;
        this.deviceID = deviceID;
        this.timestamp = timestamp;
    }

    public int getUserID() {
        return userID;
    }

    public String getBeaconID() { return beaconID; }

    public String getDeviceID() {
        return deviceID;
    }

    public String getTimestamp() { return timestamp; }
}
