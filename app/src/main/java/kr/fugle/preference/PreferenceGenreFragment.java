package kr.fugle.preference;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import kr.fugle.preference.item.Genre;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class PreferenceGenreFragment extends Fragment {

    private ArrayList<Genre> genreArrayList;

    // 차트
    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    // 색
    public static final int DEFAULT_COLOR = Color.parseColor("#DFDFDF");
    public static final int DEFAULT_DARKEN_COLOR = Color.parseColor("#DDDDDD");
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED};

    private TextView gColor[];
    private TextView gName[];
    private TextView gScore[];
    private ImageView profileImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preference_genre, container, false);

        profileImg = (ImageView) rootView.findViewById(R.id.chart_profile);

        gColor = new TextView[5];
        gName = new TextView[5];
        gScore = new TextView[5];

        for (int i = 0; i < 5; i++) {
            int colorId = getResources().getIdentifier("genre" + (i + 1) + "_color", "id", "kr.fugle");
            int nameId = getResources().getIdentifier("genre" + (i + 1) + "_name", "id", "kr.fugle");
            int scoreId = getResources().getIdentifier("genre" + (i + 1) + "_score", "id", "kr.fugle");
            gColor[i] = (TextView) rootView.findViewById(colorId);
            gName[i] = (TextView) rootView.findViewById(nameId);
            gScore[i] = (TextView) rootView.findViewById(scoreId);
        }

        genreArrayList = ((PreferenceAnalysisActivity) getActivity()).genreArrayList;

        chart = (PieChartView) rootView.findViewById(R.id.chart);
//        chart.setOnValueTouchListener(new ValueTouchListener());
        reset();

        return rootView;
    }

    private void reset() {
        chart.setCircleFillRatio(1.0f);
        hasLabels = false;
        hasLabelsOutside = false;
        hasCenterCircle = true;
        hasCenterText1 = false;
        hasCenterText2 = false;
        isExploded = false;
        hasLabelForSelected = false;
    }

    private void generateData() {

        Log.d("genreFragment----->", genreArrayList.size() + "");
        List<SliceValue> values = new ArrayList<>();


        if (genreArrayList.size() >= 5) {
            for (int i = 0; i < 4; i++) {
                SliceValue sliceValue = new SliceValue((float) genreArrayList.get(i).getCount(), COLORS[i]);
//                sliceValue.setLabel(genreArrayList.get(i).getName());
                gColor[i].setBackgroundColor(COLORS[i]);
                gName[i].setText(genreArrayList.get(i).getName());
                gScore[i].setText(genreArrayList.get(i).getCount() + "개/"
                        + "\n" + genreArrayList.get(i).getAverage() + "점");
                values.add(sliceValue);
            }
            int etcCount = 0;
            Float etcAverage = 0.0f;
            for (int i = 4; i < genreArrayList.size(); i++) {
                etcCount += genreArrayList.get(i).getCount();
                etcAverage += genreArrayList.get(i).getAverage();
            }
            etcAverage /= (genreArrayList.size() - 4);
            SliceValue sliceValue = new SliceValue((float) etcCount, DEFAULT_COLOR);
            sliceValue.setLabel("그 외");
            gColor[4].setBackgroundColor(DEFAULT_COLOR);
            gName[4].setText("그 외");
            gScore[4].setText(etcCount + "개/"
                    + "\n" + String.format("%.1f",etcAverage) + "점");
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
        data.setCenterCircleScale(0.75f);
        chart.setPieChartData(data);

        // 프로필사진
        if(!User.getInstance().getProfileImg().equals("")) {
            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(getContext().getApplicationContext())
                    .load(User.getInstance().getProfileImg())
                    .transform(circleTransform)
                    .into(profileImg);
        }
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
//            Toast.makeText(getContext().getApplicationContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        generateData();
        Log.d("mediaFragment----->", genreArrayList.size() + "");
    }
}
