package larc.ludiconprod.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LaurUser on 7/2/2016.
 */
public class Event {
    public Map<String, Boolean> usersUID = new HashMap<String,Boolean>();
    public Date date;
    public int noUsers;
    public String sport;
    public String creator;
    public String place;
    public double latitude;
    public double longitude;
    public String id;
    public String creatorName;
    public String profileImageURL;
    public int isOfficial;
    public int roomCapacity;
    public String description;
    public String message;
    public boolean active;
    public int others;
    public int points;
    public int priority;
}
