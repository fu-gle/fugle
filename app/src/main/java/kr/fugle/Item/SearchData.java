package kr.fugle.Item;

import java.util.ArrayList;

/**
 * Created by 김은진 on 2016-08-04.
 */
// 작가명, 장르명
public class SearchData {

    private static SearchData searchData;

    private ArrayList<String> searchName;

    private SearchData() {
        searchName = new ArrayList<String>();
    }

    public static SearchData getInstance() {
        if(searchData == null) {
            searchData = new SearchData();
        }
        return searchData;
    }

    public ArrayList<String> getList() {
        return searchName;
    }
}
