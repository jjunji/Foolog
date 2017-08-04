package fastcampus.team1.foolog;

import fastcampus.team1.foolog.model.Join;
import fastcampus.team1.foolog.model.Login;
import fastcampus.team1.foolog.model.LoginResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by jhjun on 2017-08-01.
 */

public interface iService {
    @POST("member/")
    Call<Join> createUser(@Body Join join);

    @POST("member/login/")
    Call<LoginResult> createLogin(@Body Login login);
}