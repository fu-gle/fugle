package kr.fugle.commonlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.PostUserLog;

/**
 * Created by 김은진 on 2016-08-05.
 */
public class CommonRecyclerAdapter extends RecyclerView.Adapter<CommonRecyclerAdapter.ViewHolder> {
    private ArrayList<Content> itemList;
    private Context commonContext;

    private ActivityStartListener activityStartListener;

    private Integer userNo;

    public CommonRecyclerAdapter(Context commonContext,
                                 ArrayList<Content> itemList,
                                 int userNo,
                                 RecyclerView recyclerView) {
        this.commonContext = commonContext;
        this.itemList = itemList;
        this.userNo = userNo;
    }

    public void setActivityStartListener(ActivityStartListener activityStartListener) {
        this.activityStartListener = activityStartListener;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final Content content = itemList.get(position);

        // 이미지
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) commonContext
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        Picasso.with(commonContext.getApplicationContext())
                .load(content.getThumbnailBig())
                .resize(metrics.widthPixels, metrics.heightPixels/3)
                .centerCrop()
                .into(viewHolder.cImageView);

        // 타이틀
        viewHolder.cTitleView.setText(content.getTitle());
        // 평점
        viewHolder.cStarView.setText("★ "+ String.format("%.1f",content.getAverage()));
        // 작가명
        viewHolder.cAuthorView.setText(content.getAuthor());

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = dateFormat.format(new Date());
                new PostUserLog(commonContext)
                        .execute("", userNo.toString(), content.getNo().toString(), time);

                Intent intent = new Intent(commonContext, DetailActivity.class);
                intent.putExtra("content", content);

                activityStartListener.activityStart(intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = View.inflate(parent.getContext(), R.layout.common_list_view_item, null);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.common_list_view_item, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 이미지, 작품명, 평점, 작가
        public CardView card;
        public ImageView cImageView;
        public TextView cTitleView;
        public TextView cStarView;
        public TextView cAuthorView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            card = (CardView) itemLayoutView.findViewById(R.id.common_card);
            cImageView = (ImageView) itemLayoutView.findViewById(R.id.cImage);
            cTitleView = (TextView) itemLayoutView.findViewById(R.id.cTitle);
            cStarView = (TextView) itemLayoutView.findViewById(R.id.cStar);
            cAuthorView = (TextView) itemLayoutView.findViewById(R.id.cAuthor);
        }
    }
}
