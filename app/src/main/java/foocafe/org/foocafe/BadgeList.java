package foocafe.org.foocafe;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class BadgeList {
    @SerializedName("badges")
    ArrayList<Badge> badges;
}
