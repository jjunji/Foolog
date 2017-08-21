package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-21.
 */

// [Post] Get List
public class AllList {

    public int pk;
    public UserInfo author;

    public class UserInfo{
        public int pk;
        public String email;
        public String nickname;
        public String profile_img;
    }

    public String text;
    public LocationInfo location;

    public class LocationInfo{
        public String title;
        public String memo;
        public Float longitude;
        public Float latitude;
    }

    public Tag[] tags;

    public class Tag{
        public String text;
        public String type;
        public String color;
    }

    public String date;
    public String photo;
}
