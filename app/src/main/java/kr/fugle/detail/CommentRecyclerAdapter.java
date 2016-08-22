package kr.fugle.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.fugle.Item.Comment;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;

/**
 * Created by hokyung on 16. 8. 16..
 */
public class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context commentContext;
    private ArrayList<Comment> list;

    public CommentRecyclerAdapter(Context commentContext,
                                  ArrayList<Comment> list,
                                  RecyclerView recyclerView){
        this.commentContext = commentContext;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new VHItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final VHItem vhItem = (VHItem)holder;

        final Comment comment = list.get(position);

        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(commentContext.getApplicationContext())
                .load(comment.getProfileImg())
                .transform(circleTransform)
                .into(vhItem.profileImg);

        vhItem.userName.setText(comment.getUserName());

        vhItem.message.setText(comment.getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VHItem extends RecyclerView.ViewHolder {

        private ImageView profileImg;
        private TextView userName;
        private EditText message;

        public VHItem(View itemView) {
            super(itemView);

            profileImg = (ImageView)itemView.findViewById(R.id.profileImg);
            userName = (TextView)itemView.findViewById(R.id.userName);
            message = (EditText)itemView.findViewById(R.id.message);
        }
    }
}
