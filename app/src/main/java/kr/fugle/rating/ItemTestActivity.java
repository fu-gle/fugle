package kr.fugle.rating;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.fugle.R;

public class ItemTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_test);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<Test> list = new ArrayList<>();
        Test[] data = new Test[3];
        data[0] = new Test("A", "a");
        data[1] = new Test("B", "b");
        data[2] = new Test("C", "c");
        for(int i=0;i<3;i++) list.add(data[i]);

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), list, R.layout.rating_test));
    }
}
