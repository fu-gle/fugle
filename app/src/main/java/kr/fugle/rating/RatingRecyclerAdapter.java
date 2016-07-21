package kr.fugle.rating;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import kr.fugle.Item.Content;
import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 19..
 */
public class RatingRecyclerAdapter extends RecyclerView.Adapter<RatingRecyclerAdapter.VHItem> {

    private Context context;
    private List<Content> list;
    Context ratingContext;
    Integer userNo;

    private OkHttpClient client = new OkHttpClient();
    final static String serverUrl = "http://52.79.147.163:8000/";

    public RatingRecyclerAdapter(Context context, List<Content> list, Context ratingContext, int userNo){
        this.context = context;
        this.list = list;
        this.ratingContext = ratingContext;
        this.userNo = userNo;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item, parent, false);
        return new VHItem(v);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {

        final boolean[] lock = {false};

        final VHItem vhItem = (VHItem)holder;

        final Content content = list.get(position);

        vhItem.no = content.getNo();

        Picasso.with(context.getApplicationContext())
                .load(content.getThumbnail())
                .into(vhItem.thumbnailImg);

        // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        vhItem.thumbnailImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) vhItem.thumbnailImg.getLayoutParams();
        params.height = metrics.heightPixels / 3;

        vhItem.title.setText(content.getTitle());

        String author = content.getAuthor1();
        if(!content.getAuthor2().equals("null")){
            author += ", " + content.getAuthor2();
        }
        vhItem.description.setText(author + " / " + content.getAge());

        String genre = content.getGenre1();
        if(!content.getGenre2().equals("null")){
            genre += ", " + content.getGenre2();
            if(!content.getGenre3().equals("null")){
                genre += ", " + content.getGenre3();
            }
        }
        vhItem.genre.setText(genre);

        vhItem.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ratingContext)
                        .title("Basic Dialog")
                        .content("contents")
                        .positiveText("positive")
                        .negativeText("negative")
                        .show();
            }
        });

        vhItem.ratingBar.setRating(content.getRating());
        vhItem.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(lock[0]){
                    Integer Rating = (int)(rating * 10);

                    content.setRating(rating);

                    Toast.makeText(context.getApplicationContext(), "작품 번호 : " + content.getNo().toString() + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                    new OkHttpPost().execute(serverUrl, userNo.toString(), content.getNo().toString(), Rating.toString());
                    lock[0] = false;
                }
            }
        });
        vhItem.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lock[0] = true;
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VHItem extends RecyclerView.ViewHolder{

        // 작품 번호
        Integer no;

        // 위젯들
        ImageView thumbnailImg;
        TextView title;
        TextView description;
        TextView genre;
        ImageView detailBtn;
        RatingBar ratingBar;

        public VHItem(View itemView) {
            super(itemView);
            thumbnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            genre = (TextView)itemView.findViewById(R.id.genre);
            detailBtn = (ImageView)itemView.findViewById(R.id.detailBtn);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }

    public class OkHttpPost extends AsyncTask<String, Void, String> {

        public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {

            // 서버로 보낼 별점 데이터
            // 별점 이외에 사용자 번호와 작품 번호도 보내야함
            String data = "userId=" + params[1] + "&webtoonId=" + params[2] + "&star=" + params[3];
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