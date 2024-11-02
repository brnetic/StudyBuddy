package com.example.studybuddy;




import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class User implements Parcelable {

    public String name;
    private boolean loggedIn;

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
    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(email);
    }



    public boolean isLoggedIn(){
        return loggedIn;
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
