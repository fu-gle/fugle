package kr.fugle.preference.item;

/**
 * Created by 김은진 on 2016-08-23.
 */
public class Media {
    private String name;    // 미디어명
    private Float average;    // 미디어 평균
    private Integer count;  // 횟수

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

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }
}
