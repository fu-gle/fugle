package kr.fugle.preference.item;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class Genre {
    String name;    // 장르명
    Float average;  // 장르 평균
    Integer count;  // 장르 본 수

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
