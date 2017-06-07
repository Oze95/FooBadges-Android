package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CheckInList {
    @SerializedName("check_ins")
    ArrayList<CheckInCredential> checkIns;
}
