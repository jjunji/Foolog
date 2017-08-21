package fastcampus.team1.foolog;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import fastcampus.team1.foolog.Dialog.CustomDialog;
import fastcampus.team1.foolog.model.AllList;
import fastcampus.team1.foolog.model.DayList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowListFragment extends Fragment {
    View view;
    Context context;
    RecyclerView recyclerView;
    String send_token;
    SharedPreferences storage;
    String shared_token;
    Typeface font;
    ListRecyclerViewAdapter adapter;
    iService service = null;

    public ShowListFragment() {
        // Required empty public constructor
    }

    public static ShowListFragment newInstance(Context mContext) {
        Bundle args = new Bundle();
        ShowListFragment fragment = new ShowListFragment();
        fragment.context = mContext;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_list, container, false);
        initNetwork();
        initView();
        setNetwork();

        return view;
    }

    private void initView() {
        storage = context.getSharedPreferences("storage", Activity.MODE_PRIVATE);
        shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;
        font = Typeface.createFromAsset(getActivity().getAssets(), "yaFontBold.ttf");

        recyclerView = view.findViewById(R.id.listRecyclerView);
    }

    private void initNetwork(){
        // okhttp log interceptor 사용
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        // 레트로핏 객체 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(iService.class);
    }

    private void setNetwork(){
        Call<List<AllList>> call = service.getAllList(send_token);
        call.enqueue(new Callback<List<AllList>>() {
            @Override
            public void onResponse(Call<List<AllList>> call, Response<List<AllList>> response) {
                // 전송결과가 정상이면
                Log.e("Write","in ====== onResponse");
                if(response.isSuccessful()){
                    List<AllList> allLists = response.body();
                    adapter = new ListRecyclerViewAdapter(allLists, context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }else{
                    int statusCode = response.code();
                    Log.i("ShowListFragment", "응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<AllList>> call, Throwable t) {
                Log.e("MyTag","error==========="+t.getMessage());
            }
        });
    }


}
