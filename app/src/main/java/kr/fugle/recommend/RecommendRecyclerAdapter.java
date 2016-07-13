package kr.fugle.recommend;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import kr.fugle.Item;
import kr.fugle.R;

/**
 * Created by hokyung on 16. 7. 12..
 */
public class RecommendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private RecommendHeader header;
    private Context context;
    private List<Item> list;
    int itemLayout;

    public RecommendRecyclerAdapter(Context context, RecommendHeader header, List<Item> list, int itemLayout){
        this.header = header;
        this.context = context;
        this.list = list;
        this.itemLayout = itemLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item_header, parent, false);
            return (RecyclerView.ViewHolder)(new VHHeader(v));
        } else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item, parent, false);
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

            final Item item = list.get(position);

            vhItem.no = item.getNo();
            Picasso.with(context.getApplicationContext())
                    .load(item.getThumbnail())
                    .into(vhItem.thumbnailImg);
            vhItem.prediction.setText(item.getPrediction().toString());
            vhItem.title.setText(item.getTitle());
//        vhItem.tag.setText("선호하는 테그 #" + item.getTag());
//        vhItem.friends.setText(item.getFriends + "님 왜 7명의 친구가 봤어요");
            vhItem.preference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s preference", Toast.LENGTH_SHORT).show();
                }
            });
            vhItem.rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getApplicationContext(), "만화 : " + vhItem.no + "'s rating", Toast.LENGTH_SHORT).show();
                }
            });
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
