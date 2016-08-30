package kr.fugle.rating;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.comment.CommentActivity;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.PostUserLog;
import kr.fugle.webconnection.PostSingleData;

/**
 * Created by hokyung on 16. 7. 24..
 */
public class RatingRecyclerAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;    // 프로그래스바

    private ArrayList<Content> list;
    private Context ratingContext;
    private AppCompatDialog dialog;
    private Integer userNo;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private CountChangeListener countChangeListener;

    public RatingRecyclerAdapter(Context ratingContext,
                                 AppCompatDialog dialog,
                                 ArrayList<Content> list,
                                 int userNo,
                                 RecyclerView recyclerView){
        this.ratingContext = ratingContext;
        this.dialog = dialog;
        this.list = list;
        this.userNo = userNo;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        // 여기가 맨 밑에 왔을 때이니 리스트 추가를 돌린다
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setCountChangeListener(CountChangeListener countChangeListener) {
        this.countChangeListener = countChangeListener;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        if(viewType == VIEW_ITEM){
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_rating, parent, false);
            vh = new ContentVH(v);
        }else{
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_progressbar, parent, false);
            vh = new ProgressVH(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ContentVH){

            final ContentVH vhItem = (ContentVH)holder;
            final Content content = list.get(position);

            // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) ratingContext
                            .getApplicationContext()
                            .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

//            vhItem.thumbnailImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if(content.getCartoon()){   // 만화의 경우
                Picasso.with(ratingContext.getApplicationContext())
                        .load(content.getThumbnailBig())
                        .resize(metrics.widthPixels, metrics.heightPixels / 3)
                        .centerInside()
                        .into(vhItem.thumbnailImg);
            }else { // 웹툰의 경우
                Picasso.with(ratingContext.getApplicationContext())
                        .load(content.getThumbnailBig())
                        .resize(metrics.widthPixels, metrics.heightPixels / 3)
                        .centerCrop()
                        .into(vhItem.thumbnailImg);
            }

            vhItem.title.setText(content.getTitle());

            vhItem.description.setText(content.getAuthor());

            vhItem.genre.setText(content.getGenre());

            // 땡땡이 버튼(overflow icon) 클릭시 dialog
            vhItem.detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(ratingContext, "clicked", Toast.LENGTH_SHORT).show();

                    dialog.show();

                    // 다이얼로그 버튼 구현
                    // 보고싶어요 버튼
                    dialog.findViewById(R.id.preference)
                            .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(ratingContext, "작품 " + content.getNo() + ". " + content.getTitle() + "를 보고싶어요", Toast.LENGTH_SHORT).show();

                            new PostSingleData(ratingContext.getApplicationContext())
                                    .execute("like/", userNo.toString(), content.getNo().toString());

                            if(content.getLike()){  // 보고싶어요가 이미 눌려있던 상태
                                User.getInstance().setLikes(User.getInstance().getLikes() - 1);
                                content.setLike(false);
                            }else{
                                User.getInstance().setLikes(User.getInstance().getLikes() + 1);
                                content.setLike(true);

                                // 보고싶어요 누른 흔적 전송
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time = dateFormat.format(new Date());
                                new PostUserLog(ratingContext.getApplicationContext())
                                        .execute("", userNo.toString(), content.getNo().toString(), time);
                            }

                            dialog.cancel();
                        }
                    });

                    // 상세정보 버튼
                    dialog.findViewById(R.id.detail)
                            .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // 상세보기 누른 흔적 전송
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String time = dateFormat.format(new Date());
                            new PostUserLog(ratingContext.getApplicationContext())
                                    .execute("", userNo.toString(), content.getNo().toString(), time);

//                            Toast.makeText(ratingContext, "작품 " + content.getNo() + " 상세정보", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ratingContext, DetailActivity.class);
                            intent.putExtra("content", content);
                            ratingContext.startActivity(intent);
                            dialog.cancel();
                        }
                    });

                    dialog.findViewById(R.id.comment)
                            .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(ratingContext, "작품 " + content.getNo() + " 코멘트", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ratingContext, CommentActivity.class);
                            intent.putExtra("contentNo", content.getNo());
                            intent.putExtra("title", content.getTitle());
                            intent.putExtra("star", content.getRating());
                            intent.putExtra("isCartoon", content.getCartoon());

                            ratingContext.startActivity(intent);

                            dialog.cancel();
                        }
                    });
                }
            });

            vhItem.ratingBar.setRating(content.getRating());
            vhItem.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser){
                        // 별점 준 갯수 증가
                        if(rating == 0){

                            // 별점 누른 흔적 전송
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String time = dateFormat.format(new Date());
                            new PostUserLog(ratingContext.getApplicationContext())
                                    .execute("", userNo.toString(), content.getNo().toString(), time);

                            if(content.getCartoon())
                                User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() - 1);
                            else
                                User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() - 1);
                            countChangeListener.subCount();
                        }else if(content.getRating() == 0){
                            if(content.getCartoon())
                                User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() + 1);
                            else
                                User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() + 1);
                            countChangeListener.addCount();
                        }

                        Integer Rating = (int)(rating * 10);

                        content.setRating(rating);

//                        Toast.makeText(ratingContext.getApplicationContext(), "작품 번호 : " + content.getNo().toString() + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                        new PostSingleData(ratingContext)
                                .execute("insert/",
                                        userNo.toString(),
                                        content.getNo().toString(),
                                        Rating.toString());
                    }
                }
            });
        } else {
            Log.d("------>","scroll bottom");
            ProgressVH vh = (ProgressVH) holder;
            vh.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContentVH extends RecyclerView.ViewHolder {

        // 위젯들
        CardView cardView;
        ImageView thumbnailImg;
        TextView title;
        TextView description;
        TextView genre;
        ImageView detailBtn;
        RatingBar ratingBar;

        public ContentVH(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.ratingCardView);
            thumbnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            genre = (TextView)itemView.findViewById(R.id.genre);
            detailBtn = (ImageView)itemView.findViewById(R.id.detailBtn);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }

    }

    public static class ProgressVH extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ProgressVH(View v){
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }

    }
}
