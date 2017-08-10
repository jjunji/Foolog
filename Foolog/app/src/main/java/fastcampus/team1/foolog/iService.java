package fastcampus.team1.foolog;

import fastcampus.team1.foolog.model.DayList;
import fastcampus.team1.foolog.model.Join;
import fastcampus.team1.foolog.model.Login;
import fastcampus.team1.foolog.model.LoginResult;
import fastcampus.team1.foolog.model.WriteCreate;
import fastcampus.team1.foolog.model.WriteListResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Call<WriteListResult> createPost(@Body WriteCreate writeCreate, @Header("Authorization") String send_token); //todo Header

    @GET("post/day/{day}/")
    Call<DayList[]> createDayList(@Path("day") String day, @Header("Authorization") String send_token); // 토큰 값만 전송 & api 뒤에 날짜입력

    // @Part() 안에있는것이 의미하는건 서버에서 안에있는 텍스트로 인식하게끔 만들어준다.(Server Api랑 일치)
    @Multipart
    @POST("post/")
    Call<WriteListResult> uploadImage(@Header("Authorization") String send_token,
                                      @Part MultipartBody.Part photo,
                                      @Part("text") RequestBody text,
                                      @Part("tags") RequestBody tags
//                                      @Part MultipartBody.Part date,
//                                      @Part MultipartBody.Part longitude,
//                                      @Part MultipartBody.Part latitude,
//                                      @Part MultipartBody.Part memo
                                        );


}