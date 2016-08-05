package kr.fugle.Item;

import java.util.LinkedList;

/**
 * Created by hokyung on 16. 8. 5..
 * 유저의 선택사항을 로그로 남겨 서버에 전송하기 위한 싱글톤 객체
 * 는 미완성인데 취소됨
 */
public class UserLog {

    private static UserLog userLog;

    LinkedList<Integer> contentLog;

    private UserLog(){
        contentLog = new LinkedList<>();
    }

    public UserLog getInstance(){
        if(userLog == null){
            userLog = new UserLog();
        }

        return userLog;
    }

    public void addLog(int contentNo){
        contentLog.add(contentNo);

        if(contentLog.size() == 10){
            sendLog();
        }
    }

    public void sendLog(){

    }
}
