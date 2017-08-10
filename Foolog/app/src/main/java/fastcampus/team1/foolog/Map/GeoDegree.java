package fastcampus.team1.foolog.Map;

import android.media.ExifInterface;

/**
 * Created by SeungHoShin on 2017. 8. 10..
 */

/**
 * TAG_GPS_LATITUDE, TAG_GPS_LATITUDE_REF, TAG_GPS_LONGITUDE, TAG_GPS_LONGITUDE_REF 값들이 우리가 원하는
 * 위도 경도값으로 나오지 않아서 위도 경도로 변환해서 보여주는 클래스이다.
 */
public class GeoDegree {


    private Float latitude, longitude;


    /**
     * 이미지의 EXIF 의 값중 위도경도를 가져오는 생성자
     */
    public GeoDegree(ExifInterface exif) {

        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {

            if (attrLATITUDE_REF.equals("N")) {
                latitude = convertToDegree(attrLATITUDE);
            } else {
                latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                longitude = convertToDegree(attrLONGITUDE);
            } else {
                longitude = 0 - convertToDegree(attrLONGITUDE);
            }
        }
    }

    /**
     * 위도 경도의 값으로 변환해주는 메소드
     */
    public Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    }

    @Override
    public String toString() {
        return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public int getLatitudeE6() {
        return (int) (latitude * 1000000);
    }

    public int getLongitudeE6() {
        return (int) (longitude * 1000000);
    }
}
