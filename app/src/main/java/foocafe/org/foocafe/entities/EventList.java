package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventList {
    @SerializedName("events")
    public ArrayList<Event> events;
}
