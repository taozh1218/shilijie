package com.jiaohe.sakamichi.xinzhiying.bean;

/**
 * Created by DIY on 2017/2/28.
 */

public class UserInfo {

    String id;
    String username;
    String password;
    String realname;
    String phone;
    String email;
    String idcard;
    String gender;
    String blood;
    String birthday;
    String company;
    String personalurl;
    String degree;
    String zodiac;
    String school;
    String profession;
    String interest;
    String oaddress;
    String caddress;
    String headimgurl;
    String signature;
    String usertype;
    String state;
    String authstate;
    String delflag;
    String regdate;
    String authdate;
    String influenceid;

    public UserInfo() {
    }

    public UserInfo(String id, String username, String password, String realname, String phone, String email, String idcard, String gender, String blood, String birthday, String company, String personalurl, String degree, String zodiac, String school, String profession, String interest, String oaddress, String caddress, String headimgurl, String signature, String usertype, String state, String authstate, String delflag, String regdate, String authdate, String influenceid) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realname = realname;
        this.phone = phone;
        this.email = email;
        this.idcard = idcard;
        this.gender = gender;
        this.blood = blood;
        this.birthday = birthday;
        this.company = company;
        this.personalurl = personalurl;
        this.degree = degree;
        this.zodiac = zodiac;
        this.school = school;
        this.profession = profession;
        this.interest = interest;
        this.oaddress = oaddress;
        this.caddress = caddress;
        this.headimgurl = headimgurl;
        this.signature = signature;
        this.usertype = usertype;
        this.state = state;
        this.authstate = authstate;
        this.delflag = delflag;
        this.regdate = regdate;
        this.authdate = authdate;
        this.influenceid = influenceid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPersonalurl() {
        return personalurl;
    }

    public void setPersonalurl(String personalurl) {
        this.personalurl = personalurl;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getOaddress() {
        return oaddress;
    }

    public void setOaddress(String oaddress) {
        this.oaddress = oaddress;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthstate() {
        return authstate;
    }

    public void setAuthstate(String authstate) {
        this.authstate = authstate;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getAuthdate() {
        return authdate;
    }

    public void setAuthdate(String authdate) {
        this.authdate = authdate;
    }

    public String getInfluenceid() {
        return influenceid;
    }

    public void setInfluenceid(String influenceid) {
        this.influenceid = influenceid;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", realname='" + realname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", idcard='" + idcard + '\'' +
                ", gender='" + gender + '\'' +
                ", blood='" + blood + '\'' +
                ", birthday='" + birthday + '\'' +
                ", company='" + company + '\'' +
                ", personalurl='" + personalurl + '\'' +
                ", degree='" + degree + '\'' +
                ", zodiac='" + zodiac + '\'' +
                ", school='" + school + '\'' +
                ", profession='" + profession + '\'' +
                ", interest='" + interest + '\'' +
                ", oaddress='" + oaddress + '\'' +
                ", caddress='" + caddress + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", signature='" + signature + '\'' +
                ", usertype='" + usertype + '\'' +
                ", state='" + state + '\'' +
                ", authstate='" + authstate + '\'' +
                ", delflag='" + delflag + '\'' +
                ", regdate='" + regdate + '\'' +
                ", authdate='" + authdate + '\'' +
                ", influenceid='" + influenceid + '\'' +
                '}';
    }
}
