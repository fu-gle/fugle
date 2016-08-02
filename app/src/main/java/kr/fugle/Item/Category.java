package kr.fugle.Item;

/**
 * Created by hokyung on 16. 8. 1..
 */
public class Category {

    Integer no;
    String name;
    String thumbnailImg;

    public Category() {
    }

    public Category(Integer no, String name, String thumbnailImg) {
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

    public String getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }
}
