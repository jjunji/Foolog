package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-01.
 */

public class LoginResult {
    UserResult user;
    public String key;

    class UserResult {
        int pk;
        String email;
        String nickname;
        String profile_img;
    }
}
