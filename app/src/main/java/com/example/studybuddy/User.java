package com.example.studybuddy;




import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class User{

    public String name;
    Authenticator authenticator = new Authenticator();
    private String id;

    public String email;
    public ArrayList<Course> selectedCourses;

    User(){

    }
    User(String name, String id, String email, ArrayList<Course> selectedCourses){
        this.name = name;
        this.email = email;
        this.id = id;
        this.selectedCourses = selectedCourses;
    }
    public void login(String email, String password){
        this.email = email;
        authenticator.loginUser(email, password);
        User temp = authenticator.getCurrentUser();
        assignUserValues(temp);

    }

    public void register(String email, String password, String name){
        this.email = email;
        this.name = name;
        this.selectedCourses = new ArrayList<Course>();
        authenticator.createUser(this, password);
        User temp = authenticator.getCurrentUser();
        assignUserValues(temp);



    }
    public void assignUserValues(User temp){
        this.email = temp.email;
        this.id = temp.id;
        this.name = temp.name;
        this.selectedCourses = temp.selectedCourses;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
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
