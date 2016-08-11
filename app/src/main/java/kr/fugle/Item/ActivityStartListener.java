package kr.fugle.Item;

import android.content.Intent;

/**
 * Created by hokyung on 16. 8. 3..
 * Fragment 등에서 startActivity를 쓰기위한 인터페이스
 */
public interface ActivityStartListener {
    public void activityStart(Intent intent);
    public void activityStart();
    public void activityFinish();
}
