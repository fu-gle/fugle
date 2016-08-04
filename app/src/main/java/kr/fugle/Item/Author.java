package kr.fugle.Item;

/**
 * Created by hokyung on 16. 8. 4..
 * 작가 정보를 담기 위한 클래스
 */
public class Author {
    private int no;
    private String name;
    private Float avgStar;
    private Integer comments;
    private String thumbnailImg;

    public Author() {
        no = comments = 0;
        name = thumbnailImg = "";
        avgStar = 0.0f;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAvgStar() {
        return avgStar;
    }

    public void setAvgStar(Float avgStar) {
        this.avgStar = avgStar;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }
}
