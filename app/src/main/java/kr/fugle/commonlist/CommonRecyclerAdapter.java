package kr.fugle.commonlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-05.
 */
public class CommonRecyclerAdapter extends RecyclerView.Adapter<CommonRecyclerAdapter.ViewHolder> {
    private ArrayList<Content> itemList;
    private Context commonContext;

    private Integer userNo;

    public CommonRecyclerAdapter(Context commonContext,
                                 ArrayList<Content> itemList,
                                 int userNo,
                                 RecyclerView recyclerView) {
        this.commonContext = commonContext;
        this.itemList = itemList;
        this.userNo = userNo;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // 이미지
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) commonContext
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        Picasso.with(commonContext.getApplicationContext())
                .load(itemList.get(position).getThumbnailSmall())
                .resize(metrics.widthPixels, metrics.heightPixels/3)
                .centerCrop()
                .into(viewHolder.cImageView);

        // 타이틀
        viewHolder.cTitleView.setText(itemList.get(position).getTitle());
        // 평점
        viewHolder.cStarView.setText("★ "+itemList.get(position).getRating());
        // 작가명
        viewHolder.cAuthorView.setText(itemList.get(position).getAuthor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.common_list_view_item, null);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 이미지, 작품명, 평점, 작가
        public ImageView cImageView;
        public TextView cTitleView;
        public TextView cStarView;
        public TextView cAuthorView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            cImageView = (ImageView) itemLayoutView.findViewById(R.id.cImage);
            cTitleView = (TextView) itemLayoutView.findViewById(R.id.cTitle);
            cStarView = (TextView) itemLayoutView.findViewById(R.id.cStar);
            cAuthorView = (TextView) itemLayoutView.findViewById(R.id.cAuthor);
        }
    }
}
