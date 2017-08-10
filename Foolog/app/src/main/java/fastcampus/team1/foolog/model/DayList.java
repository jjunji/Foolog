package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-10.
 */

public class DayList {

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
        String text;
        String type;
        String color;
    }
}
