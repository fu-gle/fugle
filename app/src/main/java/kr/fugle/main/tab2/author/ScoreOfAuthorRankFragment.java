package kr.fugle.main.tab2.author;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Author;
import kr.fugle.Item.User;
import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-08-17.
 */
public class ScoreOfAuthorRankFragment extends Fragment {
    // 리스트뷰
    private ArrayList<Author> authorArrayList;
    private RecyclerView recyclerView;
    private AuthorRankRecyclerAdapter adapter;

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_rank_count, container, false);

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getContext().getApplicationContext().getResources().getString(R.string.server_url);

        authorArrayList = new ArrayList<>();

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView) rootView.findViewById(R.id.count_rank_recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        adapter = new AuthorRankRecyclerAdapter(
                getContext(),
                authorArrayList,
                User.getInstance().getNo(),
                recyclerView);

        adapter.setActivityStartListener(new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void activityStart() {
            }

            @Override
            public void activityFinish() {

            }
        });

        // 랭킹 가져오기
        performSearch();

        recyclerView.setAdapter(adapter);

        return rootView;
    }

    // 작가명, 작품명 입력받았을때 서버로 보냄
    // 파라미터에 맞는 리스트 받아옴
    public void performSearch() {
        if (authorArrayList.isEmpty()) {
            new GetAuthorList().execute("scoreOfAuthorRank/", User.getInstance().getNo() + "");
        }

    }

    private class GetAuthorList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d("uwangg's activity", "GetAuthorList.doInBackground");

            String data = "userId=" + params[1];
            Log.d("uwangg's activity", "GetAuthorList data " + data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();

            // json 데이터가 담길 변수
            String result = "";

            try {
                // 서버 통신 실행
                Response response = client.newCall(request).execute();

                // json 형태로의 변환을 위해 { "" :  } 추가
                result = "{\"\":" + response.body().string() + "}";
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("uwangg's activity", "GetAuthorList " + s);

            if (s != null && s != "") {
                try {
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray dataList = reader.getJSONArray("");

                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject obj = dataList.getJSONObject(i);

                        Author author = new Author();
                        if (!obj.isNull("author")) {
                            author.setName(obj.getString("author"));
                        }
                        if (!obj.isNull("average")) {
                            author.setAvgStar((float) obj.getInt("average") / 1000);
                        }
                        if (!obj.isNull("count")) {
                            author.setCountStar(obj.getInt("count"));
                        }
                        authorArrayList.add(author);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (authorArrayList.size() == 0)
                return;

            adapter.notifyDataSetChanged();
        }
    }
}
