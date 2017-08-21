package fastcampus.team1.foolog.Map;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.Marker;
import fastcampus.team1.foolog.util.PermissionControl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap;


    // Default 위치, Seoul
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1초

    private Float latitude, longitude;
    private String title, memo, foodtag;

    private MapsActivity mActivity;
    boolean askPermissionOnceAgain = false;

    private List<Marker> marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext());

        // 화면 꺼짐 방지기능을 위한 셋팅
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_maps);
        mActivity = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setNetwork();

    }


    /**
     * 생명주기를 이용한 onResume에서 권한을 허가했는지 다시 검사한다.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                PermissionControl.checkPermission(mActivity);
            }
        }

    }

    /**
     * 생명주기를 이용해 onStop 상태가 되었을 경우 리소스를 줄이기 위해 GoogleApiClient의 접속을 끊어준다.
     */
    @Override
    protected void onStop() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        // 맵의 모든 마커를 지운다
        mGoogleMap.clear();
    }

    /**
     * 생명주기를 이용해 onPause 상태가 되었을 때 위치 업데이트를 중지한다
     */
    @Override
    public void onPause() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // 맵의 모든 마커를 지운다
        super.onPause();
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

        }
        // 맵의 모든 마커를 지운다
        super.onDestroy();
    }


    /**
     * 구글 맵의 기본 설정 함수
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        // 맵의 모든 마커를 지운다
        mGoogleMap.clear();

        // 처음 맵 실행시 서울의 화면을 Default값으로 보여준다
        LatLng SEOUL = new LatLng(37.56, 126.97);
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));

        mGoogleMap.getUiSettings().setCompassEnabled(true);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

//        if (latitude != null) {
//            setMarker(latitude, longitude, title, memo, foodtag);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }

            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }


//        LatLng temp = new LatLng(37.470986, 127.126705);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp,16));
//        // todo title 은 사용자가 입력 가능하게끔 만들어 준다.
//        mGoogleMap.addMarker(new MarkerOptions().position(temp).title("Marker in Sydney"));
    }

    public void setNetwork(){

        SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
        String shared_token = storage.getString("inputToken", " ");

        String send_token = "Token " + shared_token;


        // okhttp log interceptor 사용해서 자세한 로그값을 확인한다.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();


        // 레트로핏 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // 인터페이스 불러오기
        iService service = retrofit.create(iService.class);

        final Call<List<Marker>> markerList = service.createMarker(send_token);
        markerList.enqueue(new Callback<List<Marker>>() {
            @Override
            public void onResponse(Call<List<Marker>> call, Response<List<Marker>> response) {
                if (response.isSuccessful()) {

                    marker = response.body();
                    Log.e("MapsACT","marker==" + marker);
                    Log.e("MapsACT","marker[0]==" + marker.get(0).location.title);
                    Log.e("MapsACT","marker.size==" + marker.size());
                    Log.e("MapsACT","marker.get(0).tags[0].text==" + marker.get(0).tags[0].text);
                    Log.e("MapsACT","marker.get(0).tags[1].text==" + marker.get(0).tags[1].text);
                    Log.e("MapsACT","marker.get(0).tags[0].text==" + marker.get(1).tags[0].text);
                    Log.e("MapsACT","marker.get(0).tags[0].text==" + marker.get(2).tags[0].text);
                    Log.e("MapsACT","marker.get(0).tags[0].text==" + marker.get(3).tags[0].text);

                    // todo 예외처리 , location값이 없었을때의 예외 처리
                    for (int i =0; i < marker.size() ; i++) {
                        if (marker.get(i).location.latitude!=null && marker.get(i).location.longitude!=null &&
                                marker.get(i).location.title!=null && marker.get(i).location.memo!=null) {
                            if (marker.get(i) != null) {

                                setMarker(marker.get(i).location.latitude,
                                        marker.get(i).location.longitude,
                                        marker.get(i).location.title,
                                        marker.get(i).location.memo,
                                        marker.get(i).tags[0].text);
                            }
                        }
                    }



                }else{
                    int statusCode = response.code();
                    Log.i("WriteActivity", "응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<Marker>> call, Throwable t) {

            }
        });

    }

    public void setMarker(Float latitude, Float longitude, String title, String memo, String foodtag) {

        LatLng latLng = new LatLng(latitude, longitude);
        // todo 마커를 원하는 이미지로 변경해줘야함
        if (latLng!=null) {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(title);
            markerOptions.snippet(memo);
//            markerOptions.draggable(true); // 마커를 길게누르면 마커를 이동하는 기능이 활성화 된다
            switch (foodtag) {
                case "한식":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    break;
                case "일식":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    break;
                case "중식":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    break;
                case "양식":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    break;
                case "기타":
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    break;
            }

            mGoogleMap.addMarker(markerOptions); // 마커 추가
        }
    }


    /**
     * Google ConnectionCallbacks Implement 함수
     */
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {

        Log.d(TAG, "onConnected");
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

            }
        } else {

            mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    /**
     * Google onConnectionFailed Implement 함수
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }





    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                mGoogleMap.setMyLocationEnabled(true);
                            }
                        } else mGoogleMap.setMyLocationEnabled(true);

                        return;
                    }
                }
                break;
        }
    }


}
