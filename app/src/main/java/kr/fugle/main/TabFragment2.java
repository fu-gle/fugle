package kr.fugle.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment2 extends Fragment {

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Log.d("------->", "TabFragment2 setArguments " + args.getString("a"));
        Integer num = args.getInt("num");
        Log.d("------>", "num " + num);
        num++;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("------>", "Tab2 onCreateView");
        View v = inflater.inflate(R.layout.tab_fragment1, container,false);
        return v;
    }
}
