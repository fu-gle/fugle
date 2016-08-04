package kr.fugle.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import kr.fugle.R;
import kr.fugle.ranking.RankingActivity;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment2 extends Fragment {

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment2, container,false);

        v.findViewById(R.id.webtoonBtn).setOnClickListener(onClickListener);
        v.findViewById(R.id.cartoonBtn).setOnClickListener(onClickListener);
        v.findViewById(R.id.authorBtn).setOnClickListener(onClickListener);

        return v;
    }

    Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.webtoonBtn:
                    startActivity(new Intent(getContext(), RankingActivity.class));
                    break;
                case R.id.cartoonBtn:

                    break;
                case R.id.authorBtn:

                    break;
            }
        }
    };
}
