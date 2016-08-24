package kr.fugle.preference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.fugle.R;
import kr.fugle.preference.item.Tag;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class PreferenceKeywordFragment extends Fragment {

    private TextView keyword1;
    private TextView keyword2;
    private TextView keyword3;
    private TextView keyword4;
    private TextView keyword5;
    private TextView keyword6;
    private TextView keyword7;
    private TextView keyword8;
    private TextView keyword9;
    private TextView keyword10;

    ArrayList<Tag> tagArrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preference_keyword, container, false);

        tagArrayList = ((PreferenceAnalysisActivity)getActivity()).tagArrayList;
        Collections.sort(tagArrayList, new NoDescCompare());

        keyword1 = (TextView) rootView.findViewById(R.id.keyword1);
        keyword2 = (TextView) rootView.findViewById(R.id.keyword2);
        keyword3 = (TextView) rootView.findViewById(R.id.keyword3);
        keyword4 = (TextView) rootView.findViewById(R.id.keyword4);
        keyword5 = (TextView) rootView.findViewById(R.id.keyword5);
        keyword6 = (TextView) rootView.findViewById(R.id.keyword6);
        keyword7 = (TextView) rootView.findViewById(R.id.keyword7);
        keyword8 = (TextView) rootView.findViewById(R.id.keyword8);
        keyword9 = (TextView) rootView.findViewById(R.id.keyword9);
        keyword10 = (TextView) rootView.findViewById(R.id.keyword10);

        if(tagArrayList.size() >= 10) {
            keyword5.setText(tagArrayList.get(0).getName());    // 1위
            keyword6.setText(tagArrayList.get(1).getName());    // 2위
            keyword9.setText(tagArrayList.get(2).getName());    // 3위
            keyword1.setText(tagArrayList.get(3).getName());    // 4위
            keyword8.setText(tagArrayList.get(4).getName());    // 5위
            keyword7.setText(tagArrayList.get(5).getName());    // 6위
            keyword10.setText(tagArrayList.get(6).getName());    // 7위
            keyword4.setText(tagArrayList.get(7).getName());    // 8위
            keyword3.setText(tagArrayList.get(8).getName());    // 9위
            keyword2.setText(tagArrayList.get(9).getName());    // 10위
        }

        return rootView;
    }

    static class NoDescCompare implements Comparator<Tag> {

        /**
         * 내림차순(DESC)
         */
        @Override
        public int compare(Tag arg0, Tag arg1) {
            // TODO Auto-generated method stub
            return arg0.getCount() > arg1.getCount() ? -1 : arg0.getCount() < arg1.getCount() ? 1:0;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("-------->", "PreferenceKeywordFragment.onResume");
        if(tagArrayList.size() >= 10) {
            keyword5.setText(tagArrayList.get(0).getName());    // 1위
            keyword6.setText(tagArrayList.get(1).getName());    // 2위
            keyword9.setText(tagArrayList.get(2).getName());    // 3위
            keyword1.setText(tagArrayList.get(3).getName());    // 4위
            keyword8.setText(tagArrayList.get(4).getName());    // 5위
            keyword7.setText(tagArrayList.get(5).getName());    // 6위
            keyword10.setText(tagArrayList.get(6).getName());    // 7위
            keyword4.setText(tagArrayList.get(7).getName());    // 8위
            keyword3.setText(tagArrayList.get(8).getName());    // 9위
            keyword2.setText(tagArrayList.get(9).getName());    // 10위
        }
    }
}
