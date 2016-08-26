package kr.fugle.webconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.SearchData;
import kr.fugle.R;
import kr.fugle.main.tab4.LikeHateAdapter;
import kr.fugle.mystar.MyStarAdapter;
import kr.fugle.rating.RatingRecyclerAdapter;
import kr.fugle.recommend.RecommendAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 * 추천 리스트, 평가 리스트 등 리스트로 된 데이터를 받아오기 위한 클래스
 */
public class GetContentList extends AsyncTask<String, Void, String> {

    AppCompatDialog loadingDialog;

    String serverUrl;
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    ArrayList<Content> list;
    ArrayList<String> tagList;
    RecyclerView.Adapter adapter;
    int activity;
    int userNo;

    public GetContentList(Context context,
                          ArrayList<Content> list,
                          RecyclerView.Adapter adapter,
                          int activity,
                          int userNo){
        serverUrl = context.getResources().getString(R.string.server_url);
        this.list = list;
        this.adapter = adapter;
        this.activity = activity;
        this.userNo = userNo;
    }

    public GetContentList(Context context){
        serverUrl = context.getResources().getString(R.string.server_url);
        activity = -1;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    public void setLoadingDialog(AppCompatDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d("ho's activity", "GetContentList.doInBackground");

        String data;
        RequestBody body;
        Request request;

        if(params.length == 2) {    // 작가명or작품명 넘겨줄경우
            data = "searchName=" + params[1];
            body = RequestBody.create(HTML, data);
            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();
        }else if(params.length != 1) {  // 유저번호, 페이지번호, 미디어 이름까지 넘기는 경우
            data = "userId=" + params[1] + "&pageNo=" + params[2];
            if(params.length > 3 && !params[3].equals("")){
                data += "&media=" + params[3];
            }
            Log.d("----->", "GetContentList data " + data);
            body = RequestBody.create(HTML, data);
            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();
        }else{  // 맨 처음 검색용 데이터를 받아올 때
            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .build();
            Log.d("----->", "GetContentList data search");
        }

        // json 데이터가 담길 변수
        String result = "";

        try{
            // 서버 통신 실행
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            if(responseBody.contains("<!DOCTYPE html>")){
                Log.d("ho's activity", "server response error " + responseBody);
                return null;
            }
            // json 형태로의 변환을 위해 { "" :  } 추가
            if(activity == 0)
                result = responseBody;
            else {
                result = "{\"\":" + responseBody + "}";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);

        if(loadingDialog != null)
            loadingDialog.cancel();

        if(isCancelled()){
            Log.d("ho's activity", "GetContentList is canceled");
            return;
        }

        Log.d("ho's activity", "GetContentList.onPostExecute " + s);

        Content content;
        ArrayList<Content> tempList = new ArrayList<>();
        ArrayList<String> tempSearch = new ArrayList<>();

        if(s != null && s != ""){
            try{
                // 통째로 받아들여서 하나씩 자르기 위한 json object
                JSONObject reader = new JSONObject(s);

                // 하나씩 잘라서 adapter에 저장해야 한다
                JSONArray dataList = null;

                if(activity == 0)
                    dataList = reader.getJSONArray("webtoon");
                else
                    dataList = reader.getJSONArray("");

                for (int i = 0; i < dataList.length(); i++) {
                    JSONObject obj = dataList.getJSONObject(i);
                    if (!obj.isNull("searchName")) {  // 검색용 리스트 데이터
                        tempSearch.add(obj.getString("searchName"));
                        continue;
                    } else {
                        content = new Content();

                        if (!obj.isNull("id"))
                            content.setNo(obj.getInt("id"));
                        if (!obj.isNull("title"))
                            content.setTitle(obj.getString("title"));
                        if (!obj.isNull("author")) {
                            String aut = obj.getString("author");
                            aut = aut.substring(0, aut.length() - 1);
                            content.setAuthor(aut);
                        }
                        if (!obj.isNull("average"))
                            content.setAverage((float) obj.getInt("average") / 1000);
                        if (!obj.isNull("genre"))
                            content.setGenre(obj.getString("genre").substring(0, obj.getString("genre").length() - 1));
                        if (!obj.isNull("adult"))
                            content.setAdult(obj.getBoolean("adult"));
                        if (!obj.isNull("thumbnail_small"))
                            content.setThumbnailSmall(obj.getString("thumbnail_small"));
                        if (!obj.isNull("thumbnail_big"))
                            content.setThumbnailBig(obj.getString("thumbnail_big"));
                        if (!obj.isNull("star"))
                            content.setRating((float) (obj.getInt("star") * 1.0) / 10);
                        if (!obj.isNull("like") && obj.getBoolean("like"))
                            content.setLike(obj.getBoolean("like"));
                        if (!obj.isNull("recommendStar"))
                            content.setPrediction(Float.parseFloat(String.format("%.1f", Float.parseFloat(obj.getString("recommendStar")) / 1000000)));
                        if (!obj.isNull("link"))
                            content.setLink(obj.getString("link"));
                        if (!obj.isNull("tags"))
                            content.setTags(obj.getString("tags").substring(0, obj.getString("tags").length() - 1));
                        if (!obj.isNull("dontsee"))
                            content.setHate(obj.getBoolean("dontsee"));
                        if (!obj.isNull("is_cartoon"))
                            content.setCartoon(obj.getBoolean("is_cartoon"));

                        tempList.add(content);
                    }
                }

                // 추천 리스트의 경우
                if(activity == 0){
                    JSONArray tags = reader.getJSONArray("tags");

                    for(int i = 0; i < tags.length(); i++){
                        JSONObject obj = tags.getJSONObject(i);

                        if(!obj.isNull("tag") && tagList != null)
                            tagList.add(obj.getString("tag"));
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            Log.d("ho's activity", "GetContentList.onPostExecute s is null");

            if(list.size() != 0){
                Log.d("ho's activity", "list.size != 0");
                if(list.indexOf(null) != -1)
                    list.remove(list.indexOf(null));
                adapter.notifyItemRemoved(list.size());
            }

            return;
        }

        if(tempSearch.size() != 0){
            SearchData.getInstance().getList().addAll(tempSearch);
            return;
        }

        // 리스트를 추가로 불러오는 경우는 리스트 맨 뒤에 null이 들어가있다
        if(list.size() != 0){
            Log.d("ho's activity", "list.size != 0");
            list.remove(list.size() - 1);
            adapter.notifyItemRemoved(list.size());
        }

        // 리스트 추가 후 적용
        list.addAll(tempList);
        adapter.notifyDataSetChanged();

        // 각 어댑터에 로딩완료 적용
        if(activity == 0){
            // Recommend Activity
            Log.d("ho's activity", "GetContentList Recommend Activity");
            if(tagList == null || tagList.size() == 0)
                Log.d("----->", "tag is empty");
            else
                Log.d("------>", "tags 0 " + tagList.get(0) + " size " + tagList.size());
            ((RecommendAdapter)adapter).setLoaded();
        } else if(activity == 1){
            // Rating Activity
            Log.d("ho's activity", "GetContentList Rating Activity");
            ((RatingRecyclerAdapter)adapter).setLoaded();
        } else if(activity == 2){
            // MyStar Activity
            Log.d("ho's activity", "GetContentList MyStar Activity");
            ((MyStarAdapter)adapter).setLoaded();
        } else if(activity == 3){
            // Search Activity
            Log.d("uwangg's activity", "GetContentList Search Activity");
        } else if(activity == 4) {
            // WebtoonRank Activity
            Log.d("uwangg's activity", "GetContentList WebtoonRank Activity");
        } else if(activity == 5) {
            // LikeHate Activity
            Log.d("ho's activity", "GetContentList Like Activity");
            ((LikeHateAdapter)adapter).setLoaded();
        } else if(activity == 6) {
            // Tag Activity
            Log.d("ho's activity", "GetContentList Tag Activity");
        } else if(activity == 7) {
            // Comment Activity
            Log.d("ho's activity", "GetContentList Comment Activity");
        }
    }
}
