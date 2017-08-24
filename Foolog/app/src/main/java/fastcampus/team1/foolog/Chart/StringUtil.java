package fastcampus.team1.foolog.Chart;

import android.os.Build;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by SeungHoShin on 2017. 7. 27..
 */

public class StringUtil {


    public String startDate = null;
    public String endDate = null;


    public static void setHtmlText(TextView target, String text){
        // 이건 모두다 소문자로 만들어주는 기능을 하는데 , 그래야지 폰트가 FONT로 인식을 안해서 잘 적용이 된다
        target.setAllCaps(false);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            target.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }else {
            // 누가 이상 버전은 fromHtml 의 두 번째 인자로 Html.FROM_HTML_MODE_LEGACY 필요
            target.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        }
    }

}
