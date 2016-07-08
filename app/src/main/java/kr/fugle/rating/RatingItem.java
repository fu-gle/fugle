package kr.fugle.rating;

import android.graphics.drawable.Drawable;

/**
 * Created by hokyung on 16. 7. 6..
 * 별점 주는 레이아웃의 커스텀 리스트뷰에 쓰이는 요소들 getter, setter
 * 썸네일 이미지뷰, 제목 텍스트뷰, 작가 장르 등 텍스트 뷰
 */
public class RatingItem {

    private Drawable thumnailImg;
    private String title;
    private String description;
    private Float rating;

    public Drawable getThumnailImg() {
        return thumnailImg;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setThumnailImg(Drawable thumnailImg) {
        this.thumnailImg = thumnailImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getRating() { return rating; }

    public void setRating(Float rating) { this.rating = rating; }
}
