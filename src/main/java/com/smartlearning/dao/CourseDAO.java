package com.smartlearning.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartlearning.config.CassandraSession;
import com.smartlearning.model.Course;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseDao {
    private final CqlSession session;
    private PreparedStatement insertStmt;
    private PreparedStatement selectByTeacherStmt;
    private PreparedStatement deleteStmt;

    public CourseDao() {
        this.session = CassandraSession.getSession();
        prepareStatements();
    }

    private void prepareStatements() {
        // Correspond à la table courses_by_teacher
        insertStmt = session.prepare(
            "INSERT INTO smart_learning.courses_by_teacher (teacher_id, course_id, title, description, created_at) VALUES (?, ?, ?, ?, ?)"
        );
        selectByTeacherStmt = session.prepare(
            "SELECT course_id, title, description, created_at FROM smart_learning.courses_by_teacher WHERE teacher_id = ?"
        );
        deleteStmt = session.prepare(
            "DELETE FROM smart_learning.courses_by_teacher WHERE teacher_id = ? AND course_id = ?"
        );
    }

    public void createCourse(Course course) {
        if (course.getCourseId() == null) {
            course.setCourseId(UUID.randomUUID());
        }
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(Instant.now());
        }

        BoundStatement bound = insertStmt.bind(
            course.getTeacherId(),
            course.getCourseId(),
            course.getTitle(),
            course.getDescription(),
            course.getCreatedAt()
        );
        session.execute(bound);
    }

    public List<Course> getCoursesByTeacher(UUID teacherId) {
        BoundStatement bound = selectByTeacherStmt.bind(teacherId);
        List<Course> courses = new ArrayList<>();
        
        for (Row row : session.execute(bound)) {
            Course course = new Course();
            course.setTeacherId(teacherId);
            course.setCourseId(row.getUuid("course_id"));
            course.setTitle(row.getString("title"));
            course.setDescription(row.getString("description"));
            course.setCreatedAt(row.getInstant("created_at"));
            courses.add(course);
        }
        return courses;
    }

    public void deleteCourse(UUID teacherId, UUID courseId) {
        BoundStatement bound = deleteStmt.bind(teacherId, courseId);
        session.execute(bound);
    }
}