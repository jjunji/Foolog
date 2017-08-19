package fastcampus.team1.foolog.model;

/**
 * Created by SeungHoShin on 2017. 8. 14..
 */

public class Marker {


    public int pk;
    public UserInfo author;

    public String text;
    public LocationInfo location;

    public String date;
    public String photo; // String 으로 주소로 받는다

    public class UserInfo{
        public int pk;
        public String email;
        public String nickname;
        public String profile_img;
    }

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
}
