package kr.fugle.recommend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.PostUserLog;
import kr.fugle.webconnection.PostSingleData;

//import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by hokyung on 16. 7. 12..
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_PROG = 2;

    private Context recommendContext;
//    Dialog dialog;
    private ArrayList<Content> list;
    private Integer userNo;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private ActivityStartListener activityStartListener;

    public RecommendAdapter(Context recommendContext,
//                            Dialog dialog,
                            ArrayList<Content> list,
                            int userNo,
                            RecyclerView recyclerView){
        this.recommendContext = recommendContext;
//        this.dialog = dialog;
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

    public void setList(ArrayList<Content> list){
        this.list = list;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setActivityStartListener(ActivityStartListener activityStartListener) {
        this.activityStartListener = activityStartListener;
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

            // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) recommendContext
                    .getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            Picasso.with(recommendContext.getApplicationContext())
                    .load(content.getThumbnailBig())
                    .resize(metrics.widthPixels, metrics.heightPixels/3)
                    .centerCrop()
                    .into(vhItem.thumbnailImg);

            // 이미지 클릭시 상세보기로 넘어간다
            vhItem.thumbnailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 상세보기 누른 흔적 전송
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    new PostUserLog(recommendContext.getApplicationContext())
                            .execute("log/", userNo.toString(), content.getNo().toString(), time);

                    Intent intent = new Intent(recommendContext, DetailActivity.class);
                    intent.putExtra("userNo", userNo);
                    intent.putExtra("contentNo", content.getNo());
                    recommendContext.startActivity(intent);

                }
            });

            vhItem.prediction.setText(content.getPrediction().toString());
            vhItem.title.setText(content.getTitle());
        vhItem.tag.setText("선호하는 테그 #" + content.getTags());
//        vhItem.friends.setText(content.getFriends + "님 왜 7명의 친구가 봤어요");

            // 보고싶어요 버튼 보고싶어요 색 적용
            if(content.getLike()){
                vhItem.like.setTextColor(Color.parseColor("#F13839"));
            }else{
                vhItem.like.setTextColor(Color.parseColor("#777777"));
            }

            // 보고싶어요 버튼
            vhItem.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(recommendContext.getApplicationContext(), "만화 : " + vhItem.no + "'s like " + content.getLike(), Toast.LENGTH_SHORT).show();

                    // 서버로 데이터 전송
                    new PostSingleData(recommendContext.getApplicationContext())
                            .execute("like/", userNo.toString(), content.getNo().toString());

                    if(content.getLike()){  // 이미 보고싶어요가 눌렸던 상태
                        vhItem.like.setTextColor(Color.parseColor("#777777"));
                        content.setLike(false);
                    }else {
                        vhItem.like.setTextColor(Color.parseColor("#F13839"));
                        content.setLike(true);
                    }
                }
            });

            // 보기싫어요 버튼
            vhItem.hate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(recommendContext, "만화 : " + vhItem.no + "'s 보기싫어요", Toast.LENGTH_SHORT).show();
                }
            });

            // 지금볼래요 버튼
            vhItem.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(recommendContext, "만화 : " + vhItem.no + "'s 지금볼래요", Toast.LENGTH_SHORT).show();

                    // 상세보기 누른 흔적 전송
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    new PostUserLog(recommendContext.getApplicationContext())
                            .execute("log/", userNo.toString(), content.getNo().toString(), time);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getLink()));
                    activityStartListener.activityStart(intent);
                }
            });

        }else if(holder instanceof VHProgress){
            VHProgress vh = (VHProgress) holder;
            vh.progressBar.setIndeterminate(true);
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
        TextView like;
        TextView hate;
        TextView link;

        public VHItem(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.recommendCardView);
            thumbnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            prediction = (TextView)itemView.findViewById(R.id.prediction);
            title = (TextView)itemView.findViewById(R.id.title);
            tag = (TextView)itemView.findViewById(R.id.tag);
            friends = (TextView)itemView.findViewById(R.id.friends);
            like = (TextView)itemView.findViewById(R.id.like);
            hate = (TextView)itemView.findViewById(R.id.hate);
            link = (TextView)itemView.findViewById(R.id.link);
        }
    }

    public class VHProgress extends RecyclerView.ViewHolder{

        ProgressBar progressBar;

        public VHProgress(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }
}
