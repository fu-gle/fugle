package kr.fugle.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.fugle.Item.Comment;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.login.CircleTransform;
import kr.fugle.webconnection.PostSingleData;

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

        // 그 댓글이 내 댓글일 경우
        if(comment.getUserNo() == User.getInstance().getNo()){
            ImageView delete = vhItem.delete;

            delete.setVisibility(View.VISIBLE);
            delete.setClickable(true);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(commentContext, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage("정말 삭제하시겠어요?")
                            .setCancelable(true)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteComment(comment.getCommentNo());
                                    list.remove(comment);
                                    notifyDataSetChanged();
                                    dialog.cancel();

                                    User.getInstance().setComments(User.getInstance().getComments() - 1);
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AppCompatDialog dialog = builder.create();

                    dialog.show();
                }
            });
        }
    }

    private void deleteComment(Integer commentNo){
        new PostSingleData(commentContext)
                .execute("deleteComment/",
                        User.getInstance().getNo().toString(),
                        commentNo.toString());
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
        private ImageView delete;

        public VHItem(View itemView) {
            super(itemView);

            profileImg = (ImageView)itemView.findViewById(R.id.profileImg);
            userName = (TextView)itemView.findViewById(R.id.userName);
            message = (EditText)itemView.findViewById(R.id.message);
            delete = (ImageView)itemView.findViewById(R.id.delete);
        }
    }
}
