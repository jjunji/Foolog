package fastcampus.team1.foolog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jhjun on 2017-08-09.
 */

public class BaseFragment extends Fragment {

    private static Typeface typeface;

    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("BaseFragment","===================BaseFragment"+"Start");
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

}
