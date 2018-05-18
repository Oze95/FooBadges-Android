package foocafe.org.foocafe.entities


import com.google.gson.annotations.SerializedName

class RegistrationCredential(email: String, password: String) {

    @SerializedName("email")
    var email: String
        internal set

    @SerializedName("password")
    var password: String
        internal set

    init {
        this.email = email
        this.password = password
    }
}