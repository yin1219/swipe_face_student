package com.example.swipe_face_student.Model;

import java.util.Date;

public class Leave extends LeaveId {

    String student_name;
    String leave_reason;
    String leave_check;
    String leave_date;
    String leave_content;
    String class_name;
    String class_id;
    String student_id;
    String teacher_email;
    String leave_photoUrl;
    String student_registrationToken;


    Date leave_uploaddate;

    public Leave() {
    }

    public Leave(String student_name, String leave_reason, String leave_check, String leave_date, String leave_content, String class_name, String class_id, String student_id, String teacher_email, String leave_photoUrl, String student_registrationToken, Date leave_uploaddate) {
        this.student_name = student_name;
        this.leave_reason = leave_reason;
        this.leave_check = leave_check;
        this.leave_date = leave_date;
        this.leave_content = leave_content;
        this.class_name = class_name;
        this.class_id = class_id;
        this.student_id = student_id;
        this.teacher_email = teacher_email;
        this.leave_photoUrl = leave_photoUrl;
        this.student_registrationToken = student_registrationToken;
        this.leave_uploaddate = leave_uploaddate;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getLeave_reason() {
        return leave_reason;
    }

    public void setLeave_reason(String leave_reason) {
        this.leave_reason = leave_reason;
    }

    public String getLeave_check() {
        return leave_check;
    }

    public void setLeave_check(String leave_check) {
        this.leave_check = leave_check;
    }

    public String getLeave_date() {
        return leave_date;
    }

    public void setLeave_date(String leave_date) {
        this.leave_date = leave_date;
    }

    public String getLeave_content() {
        return leave_content;
    }

    public void setLeave_content(String leave_content) {
        this.leave_content = leave_content;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getTeacher_email() {
        return teacher_email;
    }

    public void setTeacher_email(String teacher_email) {
        this.teacher_email = teacher_email;
    }

    public String getLeave_photoUrl() {
        return leave_photoUrl;
    }

    public void setLeave_photoUrl(String leave_photoUrl) {
        this.leave_photoUrl = leave_photoUrl;
    }

    public String getStudent_registrationToken() {
        return student_registrationToken;
    }

    public void setStudent_registrationToken(String student_registrationToken) {
        this.student_registrationToken = student_registrationToken;
    }

    public Date getLeave_uploaddate() {
        return leave_uploaddate;
    }

    public void setLeave_uploaddate(Date leave_uploaddate) {
        this.leave_uploaddate = leave_uploaddate;
    }
}
