package kr.fugle.rating;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.R;

/**
 * Created by hokyung on 16. 7. 6..
 * 커스텀 리스트뷰를 사용하기 위한 어뎁터
 */
public class RatingAdapter extends BaseAdapter {

    private ArrayList<RatingItem> ratingItems = new ArrayList<>();

    public RatingAdapter(){

    }

    // Adapter에 쓰이는 데이터의 갯수를 리턴
    @Override
    public int getCount() {
        return ratingItems.size();
    }

    // 지정한 위치(position)에 있는 아이템을 리턴
    @Override
    public Object getItem(int position) {
        return ratingItems.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "rating_item" Layout을 inflate하여 convertView 참조 획득
        if(convertView == null){
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rating_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView thumnailImg = (ImageView) convertView.findViewById(R.id.thumbnailImg);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

        // DataSet(ratingItems)에서 position에 위치한 데이터 참조
        RatingItem ratingItem = ratingItems.get(position);

        // Item내 각 위젯에 반영
        thumnailImg.setImageDrawable(ratingItem.getThumnailImg());
        title.setText(ratingItem.getTitle());
        description.setText(ratingItem.getDescription());
        ratingBar.setRating(ratingItem.getRating());

        // 레이팅바 리스너 할당
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(context.getApplicationContext(), rating + "", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // ratingItems 리스트에 아이템 추가
    public void addItem(Drawable thumnailImg, String title, String description, Float rating){
        RatingItem ratingItem = new RatingItem();

        ratingItem.setThumnailImg(thumnailImg);
        ratingItem.setTitle(title);
        ratingItem.setDescription(description);
        ratingItem.setRating(rating);

        ratingItems.add(ratingItem);
    }
}
