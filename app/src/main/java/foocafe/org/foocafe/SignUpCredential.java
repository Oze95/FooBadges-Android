package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpCredential implements Serializable {
    @SerializedName("user_id")
    String userID;

    @SerializedName("event_id")
    String eventID;

    public SignUpCredential(String userID, String eventID){
        this.userID = userID;
        this.eventID = eventID;
    }

    public String getUserID() {
        return userID;
    }

    public String getEventID() { return eventID; }

}
