package kr.fugle.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment1 extends Fragment {

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment1, container,false);
        return rootView;
    }
}
