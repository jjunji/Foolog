package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-01.
 */

public class LoginResult {
    public UserResult user;
    public String key;

    public class UserResult {
        public int pk;
        String email;
        String nickname;
        String profile_img;
    }

}
