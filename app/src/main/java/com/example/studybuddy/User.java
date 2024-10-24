package com.example.studybuddy;




import java.util.ArrayList;


public class User{

    public String name;
    public String email;
    public ArrayList<Course> selectedCourses;


    public void login(String name, String password){

    }

    public void register(String name, String password){
        this.name = name;

    }
    public void addCourse(Course course){
        selectedCourses.add(course);
    }
    public void joinStudyGroup(StudyGroup studyGroup){

    }
    public void createStudyGroup(){

    }
    public void logout(){

    }
    public void scheduleStudySession(){

    }
    public void uploadResource(){

    }


}
