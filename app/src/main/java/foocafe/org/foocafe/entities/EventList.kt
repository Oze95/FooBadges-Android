package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class EventList {
    @SerializedName("events")
    var events: ArrayList<Event>? = null
}
