package com.example.swipe_face_student;

import android.support.annotation.NonNull;

public class LeaveId {
    public String LeaveId;

    public <T extends LeaveId> T withId(@NonNull final String id) {
        this.LeaveId = id;
        return (T) this;

    }
}