package kr.fugle.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
        int width, height;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        Picasso.with(getContext()).load(R.drawable.frog)
                .resize(width, height)
                .into((ImageView) rootView.findViewById(R.id.today_webtoon_img));

//        Picasso.with(getContext()).load(R.drawable.frog)
//                .resize(width, height)
//                .into((ImageView) rootView.findViewById(R.id.today_cartoon_img));

        Picasso.with(getContext()).load(R.drawable.frog)
                .resize(width, height)
                .into((ImageView) rootView.findViewById(R.id.today_cartoon_img));
        return rootView;
    }
}
