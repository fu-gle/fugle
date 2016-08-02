package kr.fugle.rating.category;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.Category;
import kr.fugle.R;

/**
 * Created by hokyung on 16. 8. 1..
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter {

    ArrayList<Category> categoryArrayList;
    Context categoryContext;
    CategoryClickListener categoryClickListener;

    public CategoryRecyclerAdapter(ArrayList<Category> categoryArrayList, Context categoryContext) {
        this.categoryArrayList = categoryArrayList;
        this.categoryContext = categoryContext;
    }

    public void setCategoryClickListener(CategoryClickListener categoryClickListener) {
        this.categoryClickListener = categoryClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new CategoryVH(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final CategoryVH categoryVH = (CategoryVH)holder;
        final Category category = categoryArrayList.get(position);

        // 카테고리 섬네일 이미지 지정

        // 카테고리 이름 지정
        categoryVH.name.setText(category.getName());

        // 카테고리 클릭 리스너
        categoryVH.categorySelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(categoryContext, category.getNo() + " 카테고리 " + category.getName(), Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent();
                intent.putExtra("categoryNo", category.getNo());
                categoryClickListener.startRatingActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private class CategoryVH extends RecyclerView.ViewHolder{

        LinearLayout categorySelectBtn;
        ImageView thumnailImg;
        TextView name;

        public CategoryVH(View itemView) {
            super(itemView);

            categorySelectBtn = (LinearLayout)itemView.findViewById(R.id.categorySelectBtn);
            thumnailImg = (ImageView)itemView.findViewById(R.id.thumbnailImg);
            name = (TextView)itemView.findViewById(R.id.categoryName);
        }
    }
}
