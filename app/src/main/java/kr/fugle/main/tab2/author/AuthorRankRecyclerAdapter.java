package kr.fugle.main.tab2.author;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Author;
import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-17.
 */
public class AuthorRankRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<Author> itemList;
    private Context authorRankContext;

    private ActivityStartListener activityStartListener;

    private Integer userNo;

    public AuthorRankRecyclerAdapter(Context authorRankContext,
                                     ArrayList<Author> itemList,
                                     int userNo) {
        this.authorRankContext = authorRankContext;
        this.itemList = itemList;
        this.userNo = userNo;
    }

    public void setActivityStartListener(ActivityStartListener activityStartListener) {
        this.activityStartListener = activityStartListener;
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
        }else {

            final ViewHolder viewHolder = (ViewHolder)holder;
            final Author author = itemList.get(position - 1);

            // 순위
            viewHolder.rankNum.setText(position+"");

            // 작가명
            viewHolder.rankAuthorName.setText(author.getName());

            // 평균평점
            viewHolder.average.setText("★ " + String.format("%.1f", author.getAvgStar()));

            // 평가갯수
            viewHolder.averageCount.setText(author.getCountStar()+"");

            viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(authorRankContext, AuthorContentActivity.class);
                    intent.putExtra("userNo", userNo);
                    intent.putExtra("authorName", author.getName());
//                작가에 따른 작품명 나오게 해줘야함
                    activityStartListener.activityStart(intent);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author_rank_header, parent, false);
            return new HeaderViewHolder(v);
        }else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author_rank, parent, false);
            return new ViewHolder(v);
        }
        throw  new RuntimeException("there is no type that matches the type" + viewType + ". make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 순위, 작가명, 평점, 평가갯수, 버튼 순
        public TextView rankNum;
        public TextView rankAuthorName;
        public TextView average;
        public TextView averageCount;
        public ImageView detailButton;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rankNum = (TextView) itemLayoutView.findViewById(R.id.rank_num);
            rankAuthorName = (TextView) itemLayoutView.findViewById(R.id.rank_author_name);
            average = (TextView) itemLayoutView.findViewById(R.id.average);
            averageCount = (TextView) itemLayoutView.findViewById(R.id.average_count);
            detailButton = (ImageView) itemLayoutView.findViewById(R.id.detail_button);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemLayoutView) {
            super(itemLayoutView);
        }
    }
}
