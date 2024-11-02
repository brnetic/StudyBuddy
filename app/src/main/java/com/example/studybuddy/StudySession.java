package com.example.studybuddy;

import android.location.Location;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class StudySession {
    public Location location;
    public GregorianCalendar date;
    private String studySessionId;
    public ArrayList<User> invitedMembers;
    public User creator;
    public String sessionName;
    public StudyGroup studyGroup;

    public void updateLocation(Location location){
        this.location = location;
    }
    public void updateTime(GregorianCalendar c){
        this.date = c;
    }
    public void inviteMember(User user){
        invitedMembers.add(user);
    }
    public String getId(){
        return studySessionId;
    }


}
