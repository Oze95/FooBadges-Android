package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by foocafe on 2017-05-05.
 */

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

