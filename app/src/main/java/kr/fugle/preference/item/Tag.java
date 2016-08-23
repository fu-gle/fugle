package kr.fugle.preference.item;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class Tag {
    private String name;    // 태그명
    private Integer count;  // 태그 횟수

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
