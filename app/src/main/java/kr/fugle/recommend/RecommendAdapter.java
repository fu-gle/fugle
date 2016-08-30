package kr.fugle.recommend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.main.tab3.TagActivity;
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
    private ArrayList<Content> list;
    private ArrayList<String> tagList;
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
                            ArrayList<Content> list,
                            ArrayList<String> tagList,
                            int userNo,
                            RecyclerView recyclerView){
        this.recommendContext = recommendContext;
        this.list = list;
        this.tagList = tagList;
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

            for(int i = 0; i < tagList.size(); i++){
                vhHeader.tags[i].setVisibility(View.VISIBLE);

                final String tag = tagList.get(i);

                vhHeader.tags[i].setText("#"+tag);

                vhHeader.tags[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("ho's activity", "Recommend Activity tag clicked " + tag);

                        Intent intent = new Intent(recommendContext, TagActivity.class);
                        intent.putExtra("tag", tag);

                        activityStartListener.activityStart(intent);
                    }
                });
            }

        }else if(holder instanceof VHItem){
            final VHItem vhItem = (VHItem)holder;

            final Content content = list.get(position - 1);

            vhItem.prediction.setText(content.getPrediction().toString());
            vhItem.title.setText(content.getTitle());

            String tags = content.getTags();
            String [] tagList = tags.split(",");
            tags = "";
            for(int i = 0; i < tagList.length; i++){
                tags += "#" + tagList[i];
                if(i != tagList.length - 1){
                    tags += " ";
                }
            }

            if(tags.equals("#"))
                vhItem.tag.setVisibility(View.GONE);
            else
                vhItem.tag.setText(tags);

            // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) recommendContext
                    .getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            if(content.getCartoon() == true) {
                Picasso.with(recommendContext.getApplicationContext())
                        .load(content.getThumbnailBig())
                        .resize(metrics.widthPixels, metrics.heightPixels / 3)
                        .centerInside()
                        .into(vhItem.thumbnailImg);
            } else {
                Picasso.with(recommendContext.getApplicationContext())
                        .load(content.getThumbnailBig())
                        .resize(metrics.widthPixels, metrics.heightPixels / 3)
                        .centerCrop()
                        .into(vhItem.thumbnailImg);
            }

            // 이미지 클릭시 상세보기로 넘어간다
            vhItem.thumbnailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 상세보기 누른 흔적 전송
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    new PostUserLog(recommendContext.getApplicationContext())
                            .execute("", userNo.toString(), content.getNo().toString(), time);

                    Intent intent = new Intent(recommendContext, DetailActivity.class);
                    intent.putExtra("content", content);
                    recommendContext.startActivity(intent);

                }
            });

            // 웹툰인지 만화인지 보여준다
            if(content.getCartoon()){   // 만화인 경우
                vhItem.category.setText("만화책");
            }else{  // 웹툰인경우
                vhItem.category.setText("웹툰");
            }

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
//                    Toast.makeText(recommendContext.getApplicationContext(), "만화 : " + content.getNo() + "'s like " + content.getLike(), Toast.LENGTH_SHORT).show();

                    // 서버로 데이터 전송
                    new PostSingleData(recommendContext.getApplicationContext())
                            .execute("like/", userNo.toString(), content.getNo().toString());

                    if(content.getLike()){  // 이미 보고싶어요가 눌렸던 상태
                        User.getInstance().setLikes(User.getInstance().getLikes() - 1);
                        vhItem.like.setTextColor(Color.parseColor("#777777"));
                        content.setLike(false);
                    }else {
                        vhItem.like.setTextColor(Color.parseColor("#F13839"));
                        User.getInstance().setLikes(User.getInstance().getLikes() + 1);
                        content.setLike(true);

                        // 보고싶어요 누른 흔적 전송
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = dateFormat.format(new Date());
                        new PostUserLog(recommendContext.getApplicationContext())
                                .execute("", userNo.toString(), content.getNo().toString(), time);
                    }
                }
            });

            // 보기싫어요 버튼 색 적용
            if(content.getHate()){
                vhItem.hate.setTextColor(Color.parseColor("#61CAFC"));
            }else{
                vhItem.hate.setTextColor(Color.parseColor("#777777"));
            }

            // 보기싫어요 버튼
            vhItem.hate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Toast.makeText(recommendContext, "만화 : " + content.getNo() + "'s 보기싫어요", Toast.LENGTH_SHORT).show();

                    // 서버로 데이터 전송
                    new PostSingleData(recommendContext.getApplicationContext())
                            .execute("dontsee/", userNo.toString(), content.getNo().toString());

                    if(content.getHate()){  // 이미 보기싫어요 상태..는 없지않나?
                        vhItem.hate.setTextColor(Color.parseColor("#777777"));
                        User.getInstance().setHates(User.getInstance().getHates() - 1);
                        content.setHate(false);
                    }else{  // 여기서 보기 싫어요 액션부분
//                        vhItem.hate.setTextColor(Color.parseColor("#61CAFC"));
                        User.getInstance().setHates(User.getInstance().getHates() + 1);
//                        content.setHate(true);

                        // recyclerview에서 삭제
                        list.remove(content);
                        notifyDataSetChanged();
                    }
                }
            });

            // 지금볼래요 버튼
            vhItem.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(recommendContext, "만화 : " + content.getNo() + "'s 지금볼래요", Toast.LENGTH_SHORT).show();

                    // 지금볼래요 누른 흔적 전송
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    new PostUserLog(recommendContext.getApplicationContext())
                            .execute("", userNo.toString(), content.getNo().toString(), time);

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
        else if(list.get(position - 1) == null)
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

        Button [] tags = new Button[8];

        public VHHeader(View itemView) {
            super(itemView);
            tags[0] = (Button)itemView.findViewById(R.id.tag1);
            tags[1] = (Button)itemView.findViewById(R.id.tag2);
            tags[2] = (Button)itemView.findViewById(R.id.tag3);
            tags[3] = (Button)itemView.findViewById(R.id.tag4);
            tags[4] = (Button)itemView.findViewById(R.id.tag5);
            tags[5] = (Button)itemView.findViewById(R.id.tag6);
            tags[6] = (Button)itemView.findViewById(R.id.tag7);
            tags[7] = (Button)itemView.findViewById(R.id.tag8);
        }
    }

    public class VHItem extends RecyclerView.ViewHolder{

        // 위젯들
        CardView cardView;
        ImageView thumbnailImg;
        TextView category;
        TextView prediction;
        TextView title;
        TextView tag;
        TextView like;
        TextView hate;
        TextView link;

        public VHItem(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.recommendCardView);
            thumbnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            category = (TextView)itemView.findViewById(R.id.category);
            prediction = (TextView)itemView.findViewById(R.id.prediction);
            title = (TextView)itemView.findViewById(R.id.title);
            tag = (TextView)itemView.findViewById(R.id.tag);
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
