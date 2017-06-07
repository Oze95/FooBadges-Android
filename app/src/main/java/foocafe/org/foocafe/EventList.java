package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class EventList {
    @SerializedName("events")
    ArrayList<Event> events;
}
