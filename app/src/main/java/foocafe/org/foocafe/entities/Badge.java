package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Badge implements Serializable {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("description")
    public String description;

    @SerializedName("criteria")
    public String criteria;
}