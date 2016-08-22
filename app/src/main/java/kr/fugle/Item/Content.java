package kr.fugle.Item;

import java.io.Serializable;

/**
 * Created by hokyung on 16. 7. 11..
 * 작품(웹툰, 만화) 정보를 담는 클래스
 */
public class Content implements Serializable {

    Integer no;
    String title;
    String author;
    String genre;
    String summary;
    String media;
    Boolean publish;
    String thumbnailSmall;
    String thumbnailBig;
    String link;
    Float prediction;   // 예상 별점
    Float rating;   // 내가 준 별점
    Boolean like;  // 보고싶어요 클릭 여부
    Float average;  // 평균 별점
    String tags;    // 테그 들
    Boolean adult;
    Boolean hate;
    Boolean isCartoon;  // 웹툰이면 false, 만화면 true
    Integer likeCnt;    // 보고싶어요 누른사람수

    public Content(){
        no = 0;
        title = author = genre = summary = media = thumbnailSmall = thumbnailBig = link = tags = "";
        publish = like = adult = hate = isCartoon = false;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public Boolean getHate() {
        return hate;
    }

    public void setHate(Boolean hate) {
        this.hate = hate;
    }

    public Boolean getCartoon() {
        return isCartoon;
    }

    public void setCartoon(Boolean cartoon) {
        isCartoon = cartoon;
    }

    public Integer getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }
}
