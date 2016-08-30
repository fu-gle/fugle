package kr.fugle.Intro;

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
 * Created by 김은진 on 2016-08-29.
 */
public class FirstFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menual_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext()
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        Picasso.with(getContext())
                .load(R.drawable.menual2_1)
                .resize(metrics.widthPixels, metrics.heightPixels)
                .centerCrop()
                .into(imageView);
        return rootView;
    }
}
