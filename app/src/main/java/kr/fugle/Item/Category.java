package kr.fugle.Item;

/**
 * Created by hokyung on 16. 8. 1..
 * 카테고리 정보를 담기위한 클래스
 */
public class Category {

    Integer no;
    String name;
    Integer thumbnailImg;

    public Category() {
    }

    public Category(Integer no, String name, Integer thumbnailImg) {
        this.no = no;
        this.name = name;
        this.thumbnailImg = thumbnailImg;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(Integer thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }
}
