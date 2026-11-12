package com.smartlearning.model;

import java.time.Instant;
import java.util.UUID;

/** POJO : un cours (tables courses_by_teacher et courses_by_student) */
public class Course {
    private UUID courseId;
    private UUID teacherId;
    private String teacherName;   // denormalisé dans courses_by_student
    private String title;
    private String description;
    private Instant createdAt;

    public Course() {}

    public Course(UUID teacherId, String title, String description) {
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
    }

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", teacherId=" + teacherId + ", title='" + title + "'}";
    }
}
