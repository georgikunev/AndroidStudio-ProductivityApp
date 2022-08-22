package com.example.productivityapp.model;






public class Lesson{

    private String lessonId;
    private String pdfUrl;
    private String courseId;
    public String pdfName;
    private String lessonTitle;

    public Lesson() {

    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Lesson(String lessonTitle, String pdfName, String pdfUrl, String lessonId) {
        this.lessonTitle = lessonTitle;
        this.pdfName = pdfName;
        this.pdfUrl = pdfUrl;
        this.lessonId = lessonId;
    }
}

