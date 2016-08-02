package kr.fugle.Item;

/**
 * Created by hokyung on 16. 7. 11..
 */
public class Content {

    Integer no;
    String title;
    String author1, author2;
    String genre1, genre2, genre3;
    String summary;
    String media;
    Boolean publish;
    String age;
    String thumbnailSmall;
    String thumbnailBig;
    String link;
    Float prediction;   // 예상 별점
    Float rating;   // 내가 준 별점
    Boolean heart;  // 보고싶어요 클릭 여부
    Float average;  // 평균 별

    public Content(){
        no = 0;
        title = author1 = author2 = genre1 = genre2 = genre3 = summary = media = age = thumbnailSmall = thumbnailBig = link = "";
        publish = heart = false;
        prediction = rating = average = 0.0f;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor1() {
        return author1;
    }

    public void setAuthor1(String author1) {
        this.author1 = author1;
    }

    public String getAuthor2() {
        return author2;
    }

    public void setAuthor2(String author2) {
        this.author2 = author2;
    }

    public String getGenre1() {
        return genre1;
    }

    public void setGenre1(String genre1) {
        this.genre1 = genre1;
    }

    public String getGenre2() {
        return genre2;
    }

    public void setGenre2(String genre2) {
        this.genre2 = genre2;
    }

    public String getGenre3() {
        return genre3;
    }

    public void setGenre3(String genre3) {
        this.genre3 = genre3;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getThumbnailSmall() {
        return thumbnailSmall;
    }

    public void setThumbnailSmall(String thumbnailSmall) {
        this.thumbnailSmall = thumbnailSmall;
    }

    public String getThumbnailBig() {
        return thumbnailBig;
    }

    public void setThumbnailBig(String thumbnailBig) {
        this.thumbnailBig = thumbnailBig;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Float getPrediction() {
        return prediction;
    }

    public void setPrediction(Float prediction) {
        this.prediction = prediction;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getHeart() {
        return heart;
    }

    public void setHeart(Boolean heart) {
        this.heart = heart;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }
}
