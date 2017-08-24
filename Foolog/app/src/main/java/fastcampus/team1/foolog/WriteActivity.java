package fastcampus.team1.foolog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.maps.MapsInitializer;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import fastcampus.team1.foolog.Map.GeoDegree;
import fastcampus.team1.foolog.Map.GpsInfo;
import fastcampus.team1.foolog.model.WriteListResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private ImageButton btnPost;
    private ImageView WriteImage;
    private RadioButton radioBtnKorea;
    private RadioButton radioBtnJapan;
    private RadioButton radioBtnChina;
    private RadioButton radioBtnUsa;
    private RadioButton radioBtnEtc;
    private RadioButton radioBtnGood;
    private RadioButton radioBtnSoso;
    private RadioButton radioBtnBad;
    private TextView txtFood;
    private RadioGroup rgFood;
    private RadioGroup rgTaste;
    private TextView txtTaste;
    private TextView txtAdress;
    private EditText editContent;

    String imagePath;
    Intent intent = null;
    private ImageView tagimgKorea;
    private ImageView tagimgJapan;
    private ImageView tagimgChina;
    private ImageView tagimgUsa;
    private ImageView tagimgEtc;
    private ImageView tagimgGood;
    private ImageView tagimgSoso;
    private ImageView tagimgBad;
    private EditText editTitle;
    private EditText editMemo;

    // EXIF에서 위도와 경도값을 받아오는 변수
    public float latitude;
    public float longitude;
    // 위도와 경도값을 셋팅해주는 변수
    String set_latitude;
    String set_longitude;

    private GpsInfo gps;
//    private MapsActivity maps;


    private Bitmap bitmap, rotateBitmap;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initView();
        setListener();
        setRadioGroup();
        MapsInitializer.initialize(this);

    }

    /**
     * 리스너를 달아주는 함수
     */
    private void setListener() {
        btnBack.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        WriteImage.setOnClickListener(this);
        txtAdress.setOnClickListener(this);
    }

    /**
     * FindViewbyMe 를 사용한 선언
     * xml에서 LinearLayout에 focus를 줌으로써 자동으로 입력되는걸 방지했다.
     */
    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnPost = (ImageButton) findViewById(R.id.btnPost);
        WriteImage = (ImageView) findViewById(R.id.WriteImage);
        radioBtnKorea = (RadioButton) findViewById(R.id.radioBtnKorea);
        radioBtnJapan = (RadioButton) findViewById(R.id.radioBtnJapan);
        radioBtnChina = (RadioButton) findViewById(R.id.radioBtnChina);
        radioBtnUsa = (RadioButton) findViewById(R.id.radioBtnUsa);
        radioBtnEtc = (RadioButton) findViewById(R.id.radioBtnEtc);
        radioBtnGood = (RadioButton) findViewById(R.id.radioBtnGood);
        radioBtnSoso = (RadioButton) findViewById(R.id.radioBtnSoso);
        radioBtnBad = (RadioButton) findViewById(R.id.radioBtnBad);
        txtFood = (TextView) findViewById(R.id.txtFood);
        rgFood = (RadioGroup) findViewById(R.id.rgFood);
        rgTaste = (RadioGroup) findViewById(R.id.rgTaste);
        txtTaste = (TextView) findViewById(R.id.txtTaste);
        txtAdress = (TextView) findViewById(R.id.txtAdress);
        editContent = (EditText) findViewById(R.id.editContent);
        tagimgKorea = (ImageView) findViewById(R.id.tagimgKorea);
        tagimgJapan = (ImageView) findViewById(R.id.tagimgJapan);
        tagimgChina = (ImageView) findViewById(R.id.tagimgChina);
        tagimgUsa = (ImageView) findViewById(R.id.tagimgUsa);
        tagimgEtc = (ImageView) findViewById(R.id.tagimgEtc);
        tagimgGood = (ImageView) findViewById(R.id.tagimgGood);
        tagimgSoso = (ImageView) findViewById(R.id.tagimgSoso);
        tagimgBad = (ImageView) findViewById(R.id.tagimgBad);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editMemo = (EditText) findViewById(R.id.editMemo);
    }

    /**
     * 버튼 onClick
     * 각각의 onClick 이벤트를 나타내 주었다. btnBack, btnPost은 우선 뒤로가기로 해놨다.
     * WriteImage에 클릭이 가능하게끔 Clickable을 주었다.  + txtAdress에도
     * WriteImage는 선택한 이미지를 돌려받기 위해서 starActivityforResult를 쓴다
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnBack:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            // todo if문을 넣어서 아래쪽에 태그나 내용이 없으면 post가 안되게끔 해주자
            case R.id.btnPost:
                uploadFile();
//                maps = new MapsActivity();
//                maps.setMarker(latitude, longitude, editTitle.getText().toString(),editMemo.getText().toString(),txtFood.getText().toString());
                break;
            case R.id.WriteImage:
//                WriteImage.setImageDrawable(null);
//                Crop.pickImage(this);
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "앱을 선택하세요"), 100);
                break;
        }
    }

    /**
     * startActivityForResult 를 닫아주는 함수이다.
     * Glide 사용
     * EXIF 값 추출해서 위도, 경도값 셋팅
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ){
            //&& requestCode ==Crop.REQUEST_CROP

            Uri imageUri = data.getData();

//            Uri tempUri = beginCrop(imageUri);


            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                Log.e("WriteActivity", "imageUri====" + imageUri.getPath());
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                Log.e("WriteActivity", "columnIndex===" + columnIndex);
                imagePath = cursor.getString(columnIndex);
                Log.e("WriteActivity", "imagePath===" + imagePath);
                Glide.with(getBaseContext())
                        .load(new File(imagePath))
                        .into(WriteImage);

                try {
                    // todo 만약의 사진의 위치정보 값이 없으면 자기 현재의 위치 or 마지막 위치의 정보값을 저장하게 만든다
                    ExifInterface exif = new ExifInterface(imagePath);
                    GeoDegree geoDegree = new GeoDegree(exif);
                    if (geoDegree.getLatitude() == null) {
                        gps = new GpsInfo(WriteActivity.this);
                        //GPS 사용유무 가져오기
                        if (gps.isGetLocation()) {
                            latitude = (float) gps.getLatitude();
                            longitude = (float) gps.getLongitude();
                            Toast.makeText(this,"사진의 위치정보값이 없어 현재위치를 불러옵니다.",Toast.LENGTH_LONG).show();
                            Log.e("WriteActivity", "GPS이용한 위도추출==" + latitude + "경도추출===" + longitude);
                        } else {
                            gps.showSettingsAlert();
                        }
                    } else {
                        latitude = geoDegree.getLatitude();
                        longitude = geoDegree.getLongitude();
                    }
                    setLocation();
                    //gps.stopUsingGPS();
                    Log.e("WriteActivity", "latitude===" + latitude);
                    Log.e("WriteActivity", "longitude===" + longitude);

                    Log.e("WriteActivity", "DATETIME===" + exif.getAttribute(ExifInterface.TAG_DATETIME));
                    Log.e("WriteActivity", "Longtitude REF===" + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
                    Log.e("WriteActivity", "Longtitude ===" + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    Log.e("WriteActivity", "Latitude REF===" + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                    Log.e("WriteActivity", "Latitude ===" + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setReverseGeocoder();
                cursor.close();
            } else {
                Toast.makeText(getBaseContext(), "이미지를 로드할수 없습니다", Toast.LENGTH_SHORT).show();
            }

        }
//        else if(requestCode == Crop.REQUEST_CROP){
//            handleCrop(resultCode,data);
//        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_select) {
//            WriteImage.setImageDrawable(null);
//            Crop.pickImage(this);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private Uri beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
        return destination;
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            // Activity 의 RESULT_OK값을 사용
            Log.d("handleCrop", "RESULT_OK");
            WriteImage.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.d("handleCrop", "RESULT_ERROR");
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * Reverse Geocoding
     * 위도와 경도의 값을 이용해 주소값을 추출한다.
     */
    private void setReverseGeocoder() {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
            Log.e("WriteActivity", "geocoder list1===" + list);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("WriteActivity", "GeoCoder 입출력 오류 - 서버에서 주소변환시 에러발생===");
        }
        Log.e("WriteActivity", "geocoder list2===" + list);
        if (list != null) {
            Log.e("WriteActivity", "geocoder list3===" + list);
            if (list.size() == 0) {
                if (gps.getLatitude()==0){
                    txtAdress.setText("GPS를 활성화해 위치정보를 받아오세요. \n GPS를 키신 경우 사진을 다시 선택해주세요.");
                    Toast.makeText(this,"만약 위치정보를 등록을 안하시면 \n 마커의 등록이 안됩니다.",Toast.LENGTH_LONG).show();
                }
            } else {
                txtAdress.setText(list.get(0).getAddressLine(0).toString());
                Log.e("WriteActivity", "list.get(0).getAddressLine(0).toString()==" + list.get(0).getAddressLine(0).toString());
            }
        }
    }

    /**
     * 추출한 위도 경도 값을 서버로 통신을 하기 위해 String값으로 변환해주는 함수
     */
    public void setLocation() {
        set_latitude = "" + latitude;
        set_longitude = "" + longitude;
    }


    /**
     * RadioGroup 에서 라디오 버튼 선택시 text값이 변하게 해주는 리스너 함수
     * todo 임시로 text로 하고 customview Tag를 만들면 바꾸자(gone, invisible)
     */
    public void setRadioGroup() {

        rgFood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnKorea:
                        txtFood.setText("한식");
                        tagimgKorea.setVisibility(View.VISIBLE);
                        tagimgJapan.setVisibility(View.GONE);
                        tagimgChina.setVisibility(View.GONE);
                        tagimgUsa.setVisibility(View.GONE);
                        tagimgEtc.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnJapan:
                        txtFood.setText("일식");
                        tagimgKorea.setVisibility(View.GONE);
                        tagimgJapan.setVisibility(View.VISIBLE);
                        tagimgChina.setVisibility(View.GONE);
                        tagimgUsa.setVisibility(View.GONE);
                        tagimgEtc.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnChina:
                        txtFood.setText("중식");
                        tagimgKorea.setVisibility(View.GONE);
                        tagimgJapan.setVisibility(View.GONE);
                        tagimgChina.setVisibility(View.VISIBLE);
                        tagimgUsa.setVisibility(View.GONE);
                        tagimgEtc.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnUsa:
                        txtFood.setText("양식");
                        tagimgKorea.setVisibility(View.GONE);
                        tagimgJapan.setVisibility(View.GONE);
                        tagimgChina.setVisibility(View.GONE);
                        tagimgUsa.setVisibility(View.VISIBLE);
                        tagimgEtc.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnEtc:
                        txtFood.setText("기타");
                        tagimgKorea.setVisibility(View.GONE);
                        tagimgJapan.setVisibility(View.GONE);
                        tagimgChina.setVisibility(View.GONE);
                        tagimgUsa.setVisibility(View.GONE);
                        tagimgEtc.setVisibility(View.VISIBLE);
                        break;
                }
                txtFood.setVisibility(View.GONE);
            }
        });

        rgTaste.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radioBtnGood:
                        txtTaste.setText("Good");
                        tagimgGood.setVisibility(View.VISIBLE);
                        tagimgSoso.setVisibility(View.GONE);
                        tagimgBad.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnSoso:
                        txtTaste.setText("Soso");
                        tagimgGood.setVisibility(View.GONE);
                        tagimgSoso.setVisibility(View.VISIBLE);
                        tagimgBad.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnBad:
                        txtTaste.setText("Bad");
                        tagimgGood.setVisibility(View.GONE);
                        tagimgSoso.setVisibility(View.GONE);
                        tagimgBad.setVisibility(View.VISIBLE);
                        break;
                }
                txtTaste.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 네트워크 통신하는 함수
     */
    private void uploadFile() {

        SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
        String shared_token = storage.getString("inputToken", " ");

        String send_token = "Token " + shared_token;

        Log.e("WriteActivity", "shared_token==========" + shared_token);
        Log.e("WriteActivity", "send_token==========" + send_token);

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("글작성 중입니다 잠시만 기다려주세요...");
        progressDialog.show();

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


        MultipartBody.Part photo = null;

        if (imagePath != null) {
            File file = new File(imagePath);
            Log.e("WriteActivity", "file(imagePath)====" + file);
            Log.e("WriteActivity", "file.getname===" + file.getName());
            //        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);


            // 이미지를 비트맵으로 변환하는 옵션을 만들어준다
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2; // 이미지의 사이즈를 1/2로 축소
            bitmap = BitmapFactory.decodeFile(imagePath, options); // 비트맵으로 만들어준다
            rotateBitmap = imgRotate(bitmap); // 사진을 변환하게되면 EXIF 값중 회전값이 날아가는데 이걸 완충하려고 미리 오른쪽으로 90도를 돌린다.



            // 비트맵을 바이트 어레이로 변경 --> 이미지를 축소하려면 변경해야되고 , 전송까지 하려면 변경해야된다
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Compress the bitmap to jpeg format and 50% image quality --> 크기줄인것을 압축을 하는 작업이다. (용량을줄인다)
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);



            // Create a byte array from ByteArrayOutputStream  --> JPEG 포맷을 서버와의 통신을 위해 바이트어레이로 변경
            byte[] byteArray = stream.toByteArray();


            RequestBody imageFile = RequestBody.create(MediaType.parse("image/*"), byteArray);

            photo = MultipartBody.Part.createFormData("photo", file.getName(), imageFile);
        }

//        MultipartBody.Part text = MultipartBody.Part.createFormData("text", editContent.getText().toString());
//        MultipartBody.Part tags = MultipartBody.Part.createFormData("tags",txtFood.getText().toString()+","+txtTaste.getText().toString());
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), editContent.getText().toString());
        RequestBody tags = RequestBody.create(MediaType.parse("text/plain"), txtFood.getText().toString() + "," + txtTaste.getText().toString());
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), editTitle.getText().toString());
        RequestBody memo = RequestBody.create(MediaType.parse("text/plain"), editMemo.getText().toString());
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), set_latitude);
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), set_longitude);

        Call<WriteListResult> call = service.uploadImage(send_token, photo, text, tags, title, memo, latitude, longitude);
        call.enqueue(new Callback<WriteListResult>() {
            @Override
            public void onResponse(Call<WriteListResult> call, Response<WriteListResult> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "글 작성이 완료 되었습니다", Toast.LENGTH_SHORT).show();

                    // 통신이 다끝났을때 bitmap 누수 현상을 막기 위해서 recycle을 해주었고
                    // 일부 기기에서는 recycle이 되지 않아서 null값을 따로 넣어주었다.
                    bitmap.recycle();
                    bitmap = null;

                    rotateBitmap.recycle();
                    rotateBitmap = null;

                    intent = new Intent(WriteActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    int statusCode = response.code();
                    Log.i("WriteActivity", "image 응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<WriteListResult> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        bitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);

        return bitmap;
    }


}
