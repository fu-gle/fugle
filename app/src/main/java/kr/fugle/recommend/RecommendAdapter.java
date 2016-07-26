package kr.fugle.recommend;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.PostStar;

/**
 * Created by hokyung on 16. 7. 12..
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_PROG = 2;

    private Context context;
    private List<Content> list;
    private Context recommendContext;
    RecyclerView recyclerView;
    private FragmentManager fragmentManager;
    private Integer userNo;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public RecommendAdapter(Context context,
                            List<Content> list,
                            Context recommendContext,
                            RecyclerView recyclerView,
                            FragmentManager fragmentManager,
                            int userNo){
        this.context = context;
        this.list = list;
        this.recommendContext = recommendContext;
        this.recyclerView = recyclerView;
        this.fragmentManager = fragmentManager;
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
                        // End has been reached
                        // Do something
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_header, parent, false);
            return new VHHeader(v);
        }else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
            return new VHItem(v);
        }else if(viewType == TYPE_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressbar, parent, false);
            return new VHProgress(v);
        }
        throw  new RuntimeException("there is no type that matches the type" + viewType + ". make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHHeader){

            VHHeader vhHeader = (VHHeader)holder;

        }else if(holder instanceof VHItem){
            final VHItem vhItem = (VHItem)holder;

            final Content content = list.get(position - 1);

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

            // 이미지 클릭시 상세보기로 넘어간다
            vhItem.thumbnailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(recommendContext, DetailActivity.class);
                    intent.putExtra("userNo", userNo);
                    intent.putExtra("contentNo", content.getNo());
                    recommendContext.startActivity(intent);
                }
            });

            vhItem.prediction.setText(content.getPrediction().toString());
            vhItem.title.setText(content.getTitle());
//        vhItem.tag.setText("선호하는 테그 #" + content.getTag());
//        vhItem.friends.setText(content.getFriends + "님 왜 7명의 친구가 봤어요");

            // 보고싶어요 버튼
            vhItem.preference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s preference", Toast.LENGTH_SHORT).show();
                    if(content.getHeart()){
                        vhItem.preference.setTextColor(Color.parseColor("#777777"));
                        content.setHeart(false);
                    }else {
                        vhItem.preference.setTextColor(Color.parseColor("#F13839"));
                        content.setHeart(true);
                    }
                }
            });

            // 평가하기 버튼
            vhItem.rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s rating", Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("title", content.getTitle());
                    bundle.putInt("userNo", userNo);
                    bundle.putInt("contentNo", content.getNo());
                    bundle.putFloat("rating", content.getRating());

                    RatingDialog dialog = new RatingDialog();
                    dialog.setArguments(bundle);

                    // fragment manager를 이렇게 불러도 되나?
                    // 안되면 파라미터로 받아야지뭐
                    dialog.show(fragmentManager, "tag");
                }
            });

            // 코멘트 버튼
            vhItem.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s comment", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(holder instanceof VHProgress){

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        else if(list.size() == position)
            return TYPE_PROG;
        else
            return TYPE_ITEM;
    }

    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public class VHHeader extends RecyclerView.ViewHolder{

        TextView headerText;

        public VHHeader(View itemView) {
            super(itemView);
            headerText = (TextView)itemView.findViewById(R.id.headerText);
        }
    }

    public class VHItem extends RecyclerView.ViewHolder{

        // 작품 번호
        Integer no;

        // 위젯들
        CardView cardView;
        ImageView thumbnailImg;
        TextView prediction;
        TextView title;
        TextView tag;
        TextView friends;
        TextView preference;
        TextView rating;
        TextView comment;

        public VHItem(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.recommendCardView);
            thumbnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            prediction = (TextView)itemView.findViewById(R.id.prediction);
            title = (TextView)itemView.findViewById(R.id.title);
            tag = (TextView)itemView.findViewById(R.id.tag);
            friends = (TextView)itemView.findViewById(R.id.friends);
            preference = (TextView)itemView.findViewById(R.id.preference);
            rating = (TextView)itemView.findViewById(R.id.rating);
            comment = (TextView)itemView.findViewById(R.id.comment);
        }
    }

    public class VHProgress extends RecyclerView.ViewHolder{

        ProgressBar progressBar;

        public VHProgress(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }

    public static class RatingDialog extends DialogFragment {

        String title;
        Integer userNo;
        Integer contentNo;
        Float rating;

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            title = args.getString("title", "제목없음");
            userNo = args.getInt("userNo", 0);
            contentNo = args.getInt("contentNo", 0);
            rating = args.getFloat("rating", 0.0f);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.dialog_rating, container, false);
            getDialog().setTitle(title);

            RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser){
                        Integer Rating = (int)(rating * 10);

                        Toast.makeText(getContext().getApplicationContext(), "작품 번호 : " + contentNo + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                        new PostStar().execute("insert/", userNo.toString(), contentNo.toString(), Rating.toString());

                        getDialog().cancel();
                    }
                }
            });

            return view;

        }

    }
}
