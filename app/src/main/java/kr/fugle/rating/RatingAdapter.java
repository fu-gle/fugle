package kr.fugle.rating;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 6..
 * 커스텀 리스트뷰를 사용하기 위한 어뎁터
 */
public class RatingAdapter extends BaseAdapter {

    private ArrayList<RatingItem> ratingItems = new ArrayList<>();
    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client = new OkHttpClient();

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

    // ratingItems 리스트에 아이템 추가
    //public void addItem(String imgUrl, String title, String description, Float rating){
    public void addItem(Content content){
        RatingItem ratingItem = new RatingItem();

        ratingItem.setNo(content.getNo());
        ratingItem.setImgUrl(content.getThumbnail());
        ratingItem.setTitle(content.getTitle());
        String author = content.getAuthor1();
        if(!content.getAuthor2().equals("null")){
            author += ", " + content.getAuthor2();
        }
        String genre = content.getGenre1();
        if(!content.getGenre2().equals("null")){
            genre += ", " + content.getGenre2();
            if(!content.getGenre3().equals("null")){
                genre += ", " + content.getGenre3();
            }
        }
        ratingItem.setDescription(author + " / " + content.getAge() + " / " + genre);
        ratingItem.setRating(0.0f);

        ratingItems.add(ratingItem);
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();
        final boolean[] lock = {false};

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
        final RatingItem ratingItem = ratingItems.get(pos);

        // Item내 각 위젯에 반영
        Picasso.with(context.getApplicationContext())
                .load(ratingItem.getImgUrl())
//                .resize(40, 60)
                .into(thumnailImg);
        title.setText(ratingItem.getTitle());
        description.setText(ratingItem.getDescription());
        ratingBar.setRating(ratingItem.getRating());
        Log.d("-----out", ""+ratingItem.getRating());

        // 레이팅바 리스너 할당
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
//                Toast.makeText(context.getApplicationContext(), rating + "", Toast.LENGTH_SHORT).show();
                if(lock[0]){
                    Integer Rating = (int)(rating*10);

                    ratingItem.setRating(rating);
                    Toast.makeText(context.getApplicationContext(), "작품 번호 : " + ratingItem.getNo() + ", 별점 : " + Rating, Toast.LENGTH_SHORT).show();
                    Log.d("ho's activity", "RatingAdapter.ratingBar Change Listener. 작품 번호 / 별점 : " + ratingItem.getNo() + " / " + Rating);
                    new OkHttpPost().execute(serverUrl, ratingItem.getNo().toString(), Rating.toString());
                    lock[0] = false;
                }
            }
        });

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lock[0] = true;
                return false;
            }
        });
        return convertView;
    }

    public class OkHttpPost extends AsyncTask<String, Void, String>{

        public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {

            // 서버로 보낼 별점 데이터
            // 별점 이외에 사용자 번호와 작품 번호도 보내야함
            String data = "webtoonId=" + params[1] + "&star=" + params[2];
            Log.d("OkHttpPost.data", data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(params[0] + "insert/")
                    .post(body)
                    .build();

            try{
                // 서버로 전송
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("OkHttpPost","post complete");
        }
    }
}
