package fastcampus.team1.foolog.model;

/**
 * Created by SeungHoShin on 2017. 8. 3..
 */

public class WriteCreate {

    public String text; // 해당 글의 텍스트
    public byte[] photo; // 사진 todo 물어보기 , inputstream으로 열면 서로 주고받는게 byte로 주고받는다.
    public String tags; // 태그들
    public String date; //todo 물어보기 , simple date format 변환을 해서 보내준다 yy mm
    public Float longitude; // 경도
    public Float latitude; // 위도
    public String memo; // 지역에 대한 메모
}
