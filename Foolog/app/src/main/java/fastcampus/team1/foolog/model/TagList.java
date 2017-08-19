package fastcampus.team1.foolog.model;

/**
 * Created by jhjun on 2017-08-13.
 */

public class TagList {
    public String date;
    public tagInfo count;

    public class tagInfo {
        public int 한식;
        public int 중식;
        public int 일식;
        public int 양식;
        public int 기타;
        public int Good;
        public int Soso;
        public int Bad;
    }
}
