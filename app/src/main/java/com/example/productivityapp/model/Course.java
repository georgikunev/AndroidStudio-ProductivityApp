package com.example.productivityapp.model;

import com.google.firebase.database.PropertyName;
import java.util.ArrayList;


public class Course {

    private String courseTitle;
    private String courseId;
    private String createdBy;
    private ArrayList<String> enrolledUsers;

    public Course(){

    }

    @PropertyName("lessonIds")
    private ArrayList<String> lessonIds;

    @PropertyName("lessonIds")
    public ArrayList<String> getLessonIds() {
        return lessonIds;
    }

    @PropertyName("lessonIds")
    public void setLessonIds(ArrayList<String> lessonIds) {
        this.lessonIds = lessonIds;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<String> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(ArrayList<String> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Course(String courseTitle, String courseId, ArrayList<String> lessonIds) {
        this.courseTitle = courseTitle;
        this.courseId = courseId;
        this.lessonIds = lessonIds;
    }
}
