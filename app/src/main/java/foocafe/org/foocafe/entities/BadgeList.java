package foocafe.org.foocafe.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import foocafe.org.foocafe.entities.Badge;

public class BadgeList {
    @SerializedName("badges")
    public ArrayList<Badge> badges;
}
