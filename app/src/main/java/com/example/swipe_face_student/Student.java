package com.example.swipe_face_student;

import java.util.ArrayList;

public class Student {
	public Student() {
		
	}
	ArrayList<String> class_id=new ArrayList<>();
	String student_department;
	String student_email;
	String student_id;
	String student_name;
	String student_school;
	ArrayList<ClassRecord> classRecordList;
	ArrayList<LeaveRcord> leaveRcordList;
	ArrayList<Rollcall> rollcallList;
	public ArrayList<String> getClass_id() {
		return class_id;
	}
	public void setClass_id(ArrayList<String> class_id) {
		this.class_id = class_id;
	}
	public String getStudent_department() {
		return student_department;
	}
	public void setStudent_department(String student_department) {
		this.student_department = student_department;
	}
	public String getStudent_email() {
		return student_email;
	}
	public void setStudent_email(String student_email) {
		this.student_email = student_email;
	}
	public String getStudent_id() {
		return student_id;
	}
	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public String getStudent_school() {
		return student_school;
	}
	public void setStudent_school(String student_school) {
		this.student_school = student_school;
	}
	public ArrayList<ClassRecord> getClassRecordList() {
		return classRecordList;
	}
	public void setClassRecordList(ArrayList<ClassRecord> classRecordList) {
		this.classRecordList = classRecordList;
	}
	public ArrayList<LeaveRcord> getLeaveRcordList() {
		return leaveRcordList;
	}
	public void setLeaveRcordList(ArrayList<LeaveRcord> leaveRcordList) {
		this.leaveRcordList = leaveRcordList;
	}
	public ArrayList<Rollcall> getRollcallList() {
		return rollcallList;
	}
	public void setRollcallList(ArrayList<Rollcall> rollcallList) {
		this.rollcallList = rollcallList;
	}
	
	

}
