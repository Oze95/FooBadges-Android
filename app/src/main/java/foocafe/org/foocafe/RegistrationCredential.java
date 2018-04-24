package foocafe.org.foocafe;


import com.google.gson.annotations.SerializedName;

public class RegistrationCredential {

    @SerializedName("email")
    String email;

    @SerializedName("password")
    String password;

    public RegistrationCredential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
