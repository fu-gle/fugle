package kr.fugle.Intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-30.
 */
public class FifthFragment  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menual_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        Picasso.with(getContext())
                .load(R.drawable.menual6)
                .resize(540, 960)
                .centerCrop()
                .into(imageView);
        return rootView;
    }
}
