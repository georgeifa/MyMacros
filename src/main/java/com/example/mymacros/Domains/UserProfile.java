package com.example.mymacros.Domains;

public class UserProfile {

    private boolean isVerified;
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private float weight;
    private float height;
    private String goal;
    private String excFreq;

    public UserProfile(boolean isVerified, String userName,String firstName, String lastName, String gender, int age, float weight, float height, String goal, String excFreq) {
        this.isVerified = isVerified;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.goal = goal;
        this.excFreq = excFreq;
    }

    public String getUserName() {
        return userName;
    }

    public UserProfile() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getExcFreq() {
        return excFreq;
    }

    public void setExcFreq(String excFreq) {
        this.excFreq = excFreq;
    }
}
