package fastcampus.team1.foolog;

import fastcampus.team1.foolog.model.Join;
import fastcampus.team1.foolog.model.Login;
import fastcampus.team1.foolog.model.LoginResult;
import fastcampus.team1.foolog.model.WriteCreate;
import fastcampus.team1.foolog.model.WriteListResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by jhjun on 2017-08-01.
 */

public interface iService {

    @POST("member/")
    Call<Join> createUser(@Body Join join);

    @POST("member/login/")
    Call<LoginResult> createLogin(@Body Login login);

    //todo Call 앞부분은 받는걸 써야한다
    @POST("post/")
    Call<WriteListResult> createPost(@Body WriteCreate writeCreate, @Header("Authorization") String send_token); //todo Header 에 실으기


//    @POST("post/")
//    Call<ResponseBody> createPost2(@Body WriteCreate writeCreate, @Header("Authorization") String real_token);
}