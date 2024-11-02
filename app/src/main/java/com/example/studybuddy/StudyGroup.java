package com.example.studybuddy;

import java.util.ArrayList;

public class StudyGroup {
    public Course course;
    public String name;
    public ArrayList<String> members;
    public ArrayList<String> studySessions;
    public String studyGroupId;
    public ArrayList<Resource> resources;

    StudyGroup(String name, Course course, ArrayList<String> members){
        this.name = name;
        this.course = course;
        this.members = members;
    }

    public void addMember(User user){
        members.add(user.getId());

    }
    public void removeMember(User user){
        members.remove(user.getId());
    }
    public void createNewStuddySession(){

    }
    public void removeStudySession(StudySession s){
        studySessions.remove(s.getId());
    }
    public ArrayList<StudySession> getUpcomingStudySessions(){
        return null;
    }
    public void sendMessage(ChatMessage message){

    }
    public void uploadResource(){

    }

}
