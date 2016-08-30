package kr.fugle.Intro;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-30.
 */
public class SixthFragment extends Fragment {
    AnimationDrawable animation;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menual_fragment, container, false);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.menual7));
        animation = (AnimationDrawable) imageView.getDrawable();
        animation.start();
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onPause() {
        animation.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        animation.start();
    }
}
