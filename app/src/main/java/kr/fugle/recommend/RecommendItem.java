package kr.fugle.recommend;

/**
 * Created by hokyung on 16. 7. 10..
 */
public class RecommendItem {

    private String thumnailImg;
    private float prediction;
    private String title;
    private String tag;
    private String friends;

    public String getThumnailImg() {
        return thumnailImg;
    }

    public void setThumnailImg(String thumnailImg) {
        this.thumnailImg = thumnailImg;
    }

    public float getPrediction() {
        return prediction;
    }

    public void setPrediction(float prediction) {
        this.prediction = prediction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }
}
