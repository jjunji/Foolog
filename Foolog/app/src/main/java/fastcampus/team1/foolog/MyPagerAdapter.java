package fastcampus.team1.foolog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jhjun on 2017-08-08.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    Fragment[] arr;

    public MyPagerAdapter(FragmentManager fm, Fragment[] arr) {
        super(fm);
        this.arr = arr;
    }

    @Override
    public Fragment getItem(int position) {
        return arr[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
