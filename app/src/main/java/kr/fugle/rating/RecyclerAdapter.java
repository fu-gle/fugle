package kr.fugle.rating;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.fugle.R;

/**
 * Created by hokyung on 16. 7. 11..
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    List<Test> list;
    int itemLayout;

    public RecyclerAdapter(Context context, List<Test> list, int itemLayout){
        this.context = context;
        this.list = list;
        this.itemLayout = itemLayout;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_test, parent, false);
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_test, null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        final Test data = list.get(position);
        holder.textView1.setText(data.getText1());
        holder.textView2.setText(data.getText2());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView1;
        TextView textView2;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView)itemView.findViewById(R.id.textView1);
            textView2 = (TextView)itemView.findViewById(R.id.textView2);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
        }
    }
}
