package com.jiaohe.sakamichi.xinzhiying.bean;

/**
 * Created by DIY on 2017/2/21.
 */

public class UserInfoBean {
    String username;
    String gender;
    String birthday;
    String profession;
    String company;
    String school;
    String caddress;
    String oaddress;
    String email;
    String interest;
    String signature;

    public UserInfoBean() {
    }

    public UserInfoBean(String username, String gender, String birthday, String profession, String company, String school, String caddress, String oaddress, String email, String interest, String signature) {
        this.username = username;
        this.gender = gender;
        this.birthday = birthday;
        this.profession = profession;
        this.company = company;
        this.school = school;
        this.caddress = caddress;
        this.oaddress = oaddress;
        this.email = email;
        this.interest = interest;
        this.signature = signature;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getOaddress() {
        return oaddress;
    }

    public void setOaddress(String oaddress) {
        this.oaddress = oaddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", profession='" + profession + '\'' +
                ", company='" + company + '\'' +
                ", school='" + school + '\'' +
                ", caddress='" + caddress + '\'' +
                ", oaddress='" + oaddress + '\'' +
                ", email='" + email + '\'' +
                ", interest='" + interest + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
