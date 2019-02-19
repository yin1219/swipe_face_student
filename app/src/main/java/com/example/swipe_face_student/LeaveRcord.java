package com.example.swipe_face_student;

import java.util.Date;


public class LeaveRcord {
	public LeaveRcord() {
		// TODO Auto-generated constructor stub
	}
	String class_id;
	String student_id;
	Date LeaveRcord_date;
	String LeaveRecord_reason;
	String LeaveRecord_check;
	String LeaveRecord_content;
	int [] LeaveRecord_classtime = new int [2];
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
	public Date getLeaveRcord_date() {
		return LeaveRcord_date;
	}
	public void setLeaveRcord_date(Date leaveRcord_date) {
		LeaveRcord_date = leaveRcord_date;
	}
	public String getLeaveRecord_reason() {
		return LeaveRecord_reason;
	}
	public void setLeaveRecord_reason(String leaveRecord_reason) {
		LeaveRecord_reason = leaveRecord_reason;
	}
	public String getLeaveRecord_check() {
		return LeaveRecord_check;
	}
	public void setLeaveRecord_check(String leaveRecord_check) {
		LeaveRecord_check = leaveRecord_check;
	}
	public String getLeaveRecord_content() {
		return LeaveRecord_content;
	}
	public void setLeaveRecord_content(String leaveRecord_content) {
		LeaveRecord_content = leaveRecord_content;
	}
	public int[] getLeaveRecord_classtime() {
		return LeaveRecord_classtime;
	}
	public void setLeaveRecord_classtime(int[] leaveRecord_classtime) {
		LeaveRecord_classtime = leaveRecord_classtime;
	}
	
	
	
}
