package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class BadgeList {
    @SerializedName("badges")
    var badges: ArrayList<Badge>? = null
}
