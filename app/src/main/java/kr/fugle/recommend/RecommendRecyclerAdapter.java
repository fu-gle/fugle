package kr.fugle.recommend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.PostStar;

/**
 * Created by hokyung on 16. 7. 12..
 */
public class RecommendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    final static String serverUrl = "http://52.79.147.163:8000/";
    private Context context;
    private List<Content> list;
    private Context recommendContext;
    private Integer userNo;

    public RecommendRecyclerAdapter(Context context, List<Content> list, Context recommendContext, int userNo){
        this.context = context;
        this.list = list;
        this.recommendContext = recommendContext;
        this.userNo = userNo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_header, parent, false);
            return (RecyclerView.ViewHolder)(new VHHeader(v));
        } else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
            return (RecyclerView.ViewHolder)(new VHItem(v));
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
                }
            });

            // 코멘트 버튼
            vhItem.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s comment", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position){
        return position == 0;
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
}
