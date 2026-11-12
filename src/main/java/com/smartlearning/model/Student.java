package com.smartlearning.model;

import java.time.Instant;
import java.util.UUID;

/** POJO : un étudiant (utilisé dans students_by_course). */
public class Student {
    private UUID studentId;
    private String name;
    private String email;
    private Instant enrolledAt;

    public Student() {}

    public Student(UUID studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", name='" + name + "', email='" + email + "'}";
    }
}
