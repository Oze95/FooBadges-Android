package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Badge : Serializable {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("criteria")
    var criteria: String? = null
}