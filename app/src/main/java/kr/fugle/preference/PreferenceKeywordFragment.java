package kr.fugle.preference;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    ArrayList<Tag> tagArrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preference_keyword, container, false);

        tagArrayList = ((PreferenceAnalysisActivity)getActivity()).tagArrayList;
        Collections.sort(tagArrayList, new NoDescCompare());

        TextView keyword1 = (TextView) rootView.findViewById(R.id.keyword1);
        TextView keyword2 = (TextView) rootView.findViewById(R.id.keyword2);
        TextView keyword3 = (TextView) rootView.findViewById(R.id.keyword3);
        TextView keyword4 = (TextView) rootView.findViewById(R.id.keyword4);
        TextView keyword5 = (TextView) rootView.findViewById(R.id.keyword5);
        TextView keyword6 = (TextView) rootView.findViewById(R.id.keyword6);
        TextView keyword7 = (TextView) rootView.findViewById(R.id.keyword7);
        TextView keyword8 = (TextView) rootView.findViewById(R.id.keyword8);
        TextView keyword9 = (TextView) rootView.findViewById(R.id.keyword9);

        if(tagArrayList.size() >= 9) {
            keyword5.setText(tagArrayList.get(0).getName());    // 1위
            keyword6.setText(tagArrayList.get(1).getName());    // 2위
            keyword9.setText(tagArrayList.get(2).getName());    // 3위
            keyword1.setText(tagArrayList.get(3).getName());    // 4위
            keyword8.setText(tagArrayList.get(4).getName());    // 5위
            keyword7.setText(tagArrayList.get(5).getName());    // 6위
            keyword4.setText(tagArrayList.get(6).getName());    // 7위
            keyword3.setText(tagArrayList.get(7).getName());    // 8위
            keyword2.setText(tagArrayList.get(8).getName());    // 9위
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
}
