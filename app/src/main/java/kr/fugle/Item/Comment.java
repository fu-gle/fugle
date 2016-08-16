package kr.fugle.Item;

import java.io.Serializable;

/**
 * Created by hokyung on 16. 8. 16..
 */
public class Comment implements Serializable {

    private int commentNo;
    private int userNo;
    private String userName;
    private String message;
    private String profileImg;

    public Comment() {
        commentNo = userNo = 0;
        userName = message = profileImg = "";
    }

    public Comment(int commentNo, int userNo, String userName, String message, String profileImg) {
        this.commentNo = commentNo;
        this.userNo = userNo;
        this.userName = userName;
        this.message = message;
        this.profileImg = profileImg;
    }

    public int getCommentNo() {
        return commentNo;
    }

    public void setCommentNo(int commentNo) {
        this.commentNo = commentNo;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
