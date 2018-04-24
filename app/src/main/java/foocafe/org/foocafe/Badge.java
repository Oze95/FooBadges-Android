package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Badge implements Serializable {

    @SerializedName("name")
    String name;

    @SerializedName("image")
    String image;

    @SerializedName("description")
    String description;

    @SerializedName("criteria")
    String criteria;
}