package com.example.swipe_face_student;

import java.util.Date;

public class Leave extends LeaveId{

    String student_name;
    String leave_reason;
    String leave_check;
    Date leave_date;

    public Leave(){
    }

    public Leave(String student_name, String leave_reason, String leave_check, Date leave_date){
        this.student_name = student_name;
        this.leave_reason = leave_reason;
        this.leave_check = leave_check;
        this.leave_date = leave_date;
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

    public Date getLeave_date() {
        return leave_date;
    }

    public void setLeave_date(Date leave_date) {
        this.leave_date = leave_date;
    }
//    public String getStudentName() {
//        return student_name;
//    }
//    public void setStudentName(String student_name) {
//        this.student_name = student_name;
//    }
//    public String getLeaveReason(){
//        return leave_reason;
//    }
//    public void setLeaveReason(String leaver_eason){
//        this.leave_reason = leaver_eason;
//    }
//
//    public String getLeaveCheck() {
//        return leave_check;
//    }
//    public void setLeaveCheck(String leave_check) {
//        this.leave_check = leave_check;
//    }
//
//    public Date getLeaveDate() {
//        return leave_date;
//    }
//    public void setLeaveDate(Date leave_date) {
//        this.leave_date = leave_date;
//    }


}
