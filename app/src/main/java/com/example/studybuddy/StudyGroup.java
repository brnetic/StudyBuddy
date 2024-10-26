package com.example.studybuddy;

import java.util.ArrayList;

public class StudyGroup {
    public Course course;
    public String name;
    public ArrayList<User> members;
    public ArrayList<StudySession> studySessions;
    public String studyGroupId;
    public ArrayList<Resource> resources;

    public void addMember(User user){
        members.add(user);

    }
    public void removeMember(User user){
        members.remove(user);
    }
    public void createNewStuddySession(){

    }
    public void removeStudySession(StudySession s){
        studySessions.remove(s);
    }
    public ArrayList<StudySession> getUpcomingStudySessions(){
        return null;
    }
    public void sendMessage(ChatMessage message){

    }
    public void uploadResource(){

    }

}
