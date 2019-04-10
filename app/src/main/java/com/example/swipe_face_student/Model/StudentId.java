package com.example.swipe_face_student.Model;

import android.support.annotation.NonNull;

public class StudentId {
    public String StudentId;

    public <Y extends com.example.swipe_face_student.Model.StudentId> Y withId(@NonNull final String id){
        this.StudentId = id;
        return (Y) this;
    }
}
