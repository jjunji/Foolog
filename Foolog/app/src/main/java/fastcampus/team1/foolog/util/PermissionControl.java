package fastcampus.team1.foolog.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by SeungHoShin on 2017. 8. 1..
 */

public class PermissionControl {

    // 위치제공자 사용을 위한 권한처리
    private static final int REQ_PERMISSION = 10012312;

    private static String permissions[] = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    public static void checkVersion(Activity activity){
        // 마시멜로 이상버전에서만 런타임 권한체크
        if(Build.VERSION.SDK_INT >= M) {
            checkPermission(activity);
        }else {
            callInit(activity);
        }
    }


    @TargetApi(M)
    public static void checkPermission(Activity activity){
        //1 권한체크 - 특정권한이 있는지 시스템에 물어본다
        boolean denied = false;
        for(String perm : permissions){
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                denied = true;
                break;
            }
        }
        if(denied){
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            activity.requestPermissions(permissions ,REQ_PERMISSION);
        }else{
            callInit(activity);
        }
    }

    public static void onResult(Activity activity, int requestCode, @NonNull int[] grantResults){
        if(requestCode == REQ_PERMISSION){
            boolean granted = true;
            for(int grant : grantResults){
                if(grant != PackageManager.PERMISSION_GRANTED){
                    granted = false;
                    break;
                }
            }
            // 3.1 사용자가 승인을 했음
            if(granted){
                callInit(activity);
            }else{
                Toast.makeText(activity, "권한요청을 승인하셔야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void callInit(Activity activity){
        // 액티비티가 callback 을 구현했는지 확인
        if(activity instanceof CallBack)
            // activity를 넘겨주면 중간에 implement하거나 , 상속받은 모든 객체로 다 캐스팅 된다 . 그래서 CallBack으로 캐스팅
            ((CallBack)activity).init();
        else // 아니라면 런타임 예외를 발생시켜준다
            throw new RuntimeException("must implement this.CallBack");
    }

    public interface CallBack {
        public void init();
    }



    
}
