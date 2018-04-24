package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CheckInList {
    @SerializedName("check_ins")
    public ArrayList<CheckInCredential> checkIns;
}
