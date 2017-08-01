package fastcampus.team1.foolog;

import fastcampus.team1.foolog.model.Login;
import fastcampus.team1.foolog.model.LoginResult;
import fastcampus.team1.foolog.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by jhjun on 2017-08-01.
 */

public interface iService {
    @POST("member/")
    Call<User> createUser(@Body User user);

    @POST("member/login/")
    Call<LoginResult> createLogin(@Body Login login);
}