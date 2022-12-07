package com.example.chat.bojo;

public class UserStateBojo {
    String currentDate;
    String CurrentTime;
    String state;
    public UserStateBojo(String currentDate, String currentTime, String state) {
        this.currentDate = currentDate;
        CurrentTime = currentTime;
        this.state = state;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
