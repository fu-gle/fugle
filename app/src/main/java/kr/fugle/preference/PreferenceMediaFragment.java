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
import kr.fugle.preference.item.Media;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class PreferenceMediaFragment extends Fragment {

    private ArrayList<Media> mediaArrayList;

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

    // xml it
//    private TextView media1Color;   // 색
//    private TextView media2Color;
//    private TextView media3Color;
//    private TextView media1Name;    // 미디어명
//    private TextView media2Name;
//    private TextView media3Name;
//    private TextView media1Score;   // 미디어별 포인트
//    private TextView media2Score;
//    private TextView media3Score;
    private TextView mColor[];
    private TextView mName[];
    private TextView mScore[];
    private ImageView profileImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preference_media, container, false);

//        media1Color = (TextView) rootView.findViewById(R.id.media1_color);   // 색
//        media2Color = (TextView) rootView.findViewById(R.id.media2_color);
//        media3Color = (TextView) rootView.findViewById(R.id.media3_color);
//        media1Name = (TextView) rootView.findViewById(R.id.media1_name);
//        media2Name = (TextView) rootView.findViewById(R.id.media2_name);
//        media3Name = (TextView) rootView.findViewById(R.id.media3_name);
//        media1Score = (TextView) rootView.findViewById(R.id.media1_score);
//        media2Score = (TextView) rootView.findViewById(R.id.media2_score);
//        media3Score = (TextView) rootView.findViewById(R.id.media3_score);
        profileImg = (ImageView) rootView.findViewById(R.id.chart_profile);

        mColor = new TextView[3];
        mName = new TextView[3];
        mScore = new TextView[3];
        for(int i=0 ; i<3 ; i++) {
            int colorId = getResources().getIdentifier( "media"+(i+1)+"_color","id","kr.fugle");
            int nameId = getResources().getIdentifier( "media"+(i+1)+"_name","id","kr.fugle");
            int scoreId = getResources().getIdentifier( "media"+(i+1)+"_score","id","kr.fugle");
            mColor[i] = (TextView) rootView.findViewById(colorId);
            mName[i] = (TextView) rootView.findViewById(nameId);
            mScore[i] = (TextView) rootView.findViewById(scoreId);
        }

        mediaArrayList = ((PreferenceAnalysisActivity)getActivity()).mediaArrayList;

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
        List<SliceValue> values = new ArrayList<>();

        for(int i=0 ; i<mediaArrayList.size() ; i++) {
            SliceValue sliceValue = new SliceValue((float)mediaArrayList.get(i).getCount(), COLORS[i]);
//            sliceValue.setLabel(mediaArrayList.get(i).getName());
            mColor[i].setBackgroundColor(COLORS[i]);
            mName[i].setText(mediaArrayList.get(i).getName());
            mScore[i].setText(mediaArrayList.get(i).getCount()+"개/"
                                +mediaArrayList.get(i).getAverage()+"점");
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
//        data.setCenterText1("테스트");
//        data.setCenterText2("테스트2");
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
        Log.d("mediaFragment----->", mediaArrayList.size()+"");
    }
}
