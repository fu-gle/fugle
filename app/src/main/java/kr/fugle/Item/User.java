package kr.fugle.Item;

import android.util.Log;

/**
 * Created by hokyung on 16. 7. 14..
 * 유저 정보를 갖고있기 위한 클래스
 */
public class User {

    private static User user;

    private Integer no;
    private String email;
    private String passwd;
    private String name;
    private String gender;
    private String primaryKey;
    private String profileImg;
    private String message;
    private String profileBackground;
    private Integer comments;
    private Integer likes;
    private Integer hates;
    private Integer webtoonStars;
    private Integer cartoonStars;
    private Integer logCount;

    private User(){
        no = comments = likes = hates = webtoonStars = cartoonStars = logCount = 0;
        email = passwd = name = gender = primaryKey = profileImg = message = profileBackground = "";
    }

    public static User getInstance(){

        // 앱 실행시
        if(user != null)
            return user;

        // 세션으로 불렸을 시
        user = new User();
        return user;
    }

    public static void destroy(){

        Log.d("ho's activity", "user destroyed");

        if(user != null)
            user = null;

        System.gc();
    }

    public void setAttributes(int no,
                              String name,
                              String primaryKey,
                              String profileImg,
                              String message){
        this.no = no;
        this.name = name;
        this.primaryKey = primaryKey;
        this.profileImg = profileImg;
        this.message = message;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileBackground() {
        return profileBackground;
    }

    public void setProfileBackground(String profileBackground) {
        this.profileBackground = profileBackground;
    }

    public Integer getWebtoonStars() {
        return webtoonStars;
    }

    public void setWebtoonStars(Integer webtoonStars) {
        this.webtoonStars = webtoonStars;
    }

    public Integer getCartoonStars() {
        return cartoonStars;
    }

    public void setCartoonStars(Integer cartoonStars) {
        this.cartoonStars = cartoonStars;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getHates() {
        return hates;
    }

    public void setHates(Integer hates) {
        this.hates = hates;
    }

    public Integer getLogCount() {
        return logCount;
    }

    public void setLogCount(Integer logCount) {
        this.logCount = logCount;
    }
}
