package fastcampus.team1.foolog.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SeungHoShin on 2017. 8. 3..
 */

public class WriteListResult {

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
        public String memo;
        public Float longitude;
        public Float latitude;
    }

    // todo 태그 부분 체크

    public Tag tags;

    public class Tag{
       ArrayList<tag>tag;
        public class tag{
            String text;
            String type;
            String color;
        }
    }
    // todo date 타입이 맞는지 , photo타입은 어떻게 해야하는지
    public Date date;
    public String photo;

}
