package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {
    @SerializedName("title")
    public String title;

    @SerializedName("subtitle")
    public String subtitle;

    @SerializedName("image")
    public String image;

    @SerializedName("date")
    public String date;

    @SerializedName("start")
    public String time;

    @SerializedName("description")
    public String description;

    @SerializedName("checkmark")
    public Boolean checkmark = false;

    @SerializedName("id")
    public String event;

    @SerializedName("url")
    public String url;
}