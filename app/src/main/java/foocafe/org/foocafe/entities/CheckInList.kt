package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class CheckInList {
    @SerializedName("check_ins")
    var checkIns: ArrayList<CheckInCredential>? = null
}
