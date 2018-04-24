package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Event implements Serializable {
    @SerializedName("title")
    String title;

    @SerializedName("subtitle")
    String subtitle;

    @SerializedName("image")
    String image;

    @SerializedName("date")
    String date;

    @SerializedName("start")
    String time;

    @SerializedName("description")
    String description;

    @SerializedName("checkmark")
    Boolean checkmark = false;

    @SerializedName("id")
    String event;

    @SerializedName("url")
    String url;
}