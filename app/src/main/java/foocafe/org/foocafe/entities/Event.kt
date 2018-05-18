package foocafe.org.foocafe.entities

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Event : Serializable {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("subtitle")
    var subtitle: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("start")
    var time: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("checkmark")
    var checkmark: Boolean? = false

    @SerializedName("id")
    var event: String? = null

    @SerializedName("url")
    var url: String? = null
}