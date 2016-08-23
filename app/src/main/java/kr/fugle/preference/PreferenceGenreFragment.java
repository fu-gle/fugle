package kr.fugle.preference;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class PreferenceGenreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preference_genre, container, false);

        return rootView;
    }
}
