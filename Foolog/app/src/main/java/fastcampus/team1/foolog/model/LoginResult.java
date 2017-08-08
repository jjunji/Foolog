package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-01.
 */

public class LoginResult {
    public UserResult user;
    public String key;

    public static class UserResult { //todo pk값을 전역으로 사용하기 위해서 static으로 해놓았다.
        public int pk;
        public String email;
        public String nickname;
        public String profile_img;
    }

}
