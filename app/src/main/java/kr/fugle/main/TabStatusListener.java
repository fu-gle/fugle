package kr.fugle.main;

import java.util.ArrayList;

import kr.fugle.Item.Content;

/**
 * Created by hokyung on 16. 7. 27..
 */
public interface TabStatusListener {
    public void setContentList(ArrayList<Content> list);
    public ArrayList<Content> getContentList();
    public void setPageNo(int pageNum);
    public int getPageNo();
    public boolean getRefresh();
    public void setRefresh(boolean re);
}
