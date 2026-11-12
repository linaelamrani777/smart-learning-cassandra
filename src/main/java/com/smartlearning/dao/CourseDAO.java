package com.smartlearning.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartlearning.model.Course;

/**
 * DAO des cours (table courses_by_teacher). Repond à R1.
 * Utilisation : new CourseDAO(CassandraSession.getSession())
 */
public class CourseDAO {
    private static final String TABLE = "smart_learning.courses_by_teacher";

    private final CqlSession session;
    private final PreparedStatement insertStmt;
    private final PreparedStatement updateStmt;
    private final PreparedStatement selectByTeacherStmt;
    private final PreparedStatement selectOneStmt;
    private final PreparedStatement deleteStmt;

    public CourseDAO(CqlSession session) {
        this.session = session;
        insertStmt = session.prepare("INSERT INTO " + TABLE
                + " (teacher_id, course_id, title, description, created_at) VALUES (?, ?, ?, ?, ?)");
        updateStmt = session.prepare("UPDATE " + TABLE
                + " SET title = ?, description = ? WHERE teacher_id = ? AND course_id = ?");
        selectByTeacherStmt = session.prepare("SELECT course_id, title, description, created_at FROM "
                + TABLE + " WHERE teacher_id = ?");
        selectOneStmt = session.prepare("SELECT course_id, title, description, created_at FROM "
                + TABLE + " WHERE teacher_id = ? AND course_id = ?");
        deleteStmt = session.prepare("DELETE FROM " + TABLE + " WHERE teacher_id = ? AND course_id = ?");
    }

    /** CREATE : genere l'id et la date si absents */
    public Course create(Course course) {
        try {
            if (course.getCourseId() == null) course.setCourseId(UUID.randomUUID());
            if (course.getCreatedAt() == null) course.setCreatedAt(Instant.now());
            session.execute(insertStmt.bind(course.getTeacherId(), course.getCourseId(),
                    course.getTitle(), course.getDescription(), course.getCreatedAt()));
            return course;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la création du cours", e);
        }
    }

    /** R1 : cours d'un enseignant, tries du plus recent au plus ancien */
    public List<Course> findByTeacher(UUID teacherId) {
        try {
            List<Course> result = new ArrayList<>();
            for (Row row : session.execute(selectByTeacherStmt.bind(teacherId))) {
                result.add(map(row, teacherId));
            }
            // course_id est un uuid aleatoire : l'ordre de clustering n'est pas chronologique
            result.sort(Comparator.comparing(Course::getCreatedAt,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            return result;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la lecture des cours de l'enseignant " + teacherId, e);
        }
    }

    /** READ : un cours (cle complete : teacher_id + course_id) */
    public Optional<Course> findById(UUID teacherId, UUID courseId) {
        try {
            Row row = session.execute(selectOneStmt.bind(teacherId, courseId)).one();
            return row == null ? Optional.empty() : Optional.of(map(row, teacherId));
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la lecture du cours " + courseId, e);
        }
    }

    /** UPDATE : modifie titre et description */
    public void update(Course course) {
        try {
            session.execute(updateStmt.bind(course.getTitle(), course.getDescription(),
                    course.getTeacherId(), course.getCourseId()));
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la mise à jour du cours " + course.getCourseId(), e);
        }
    }

    /** DELETE : supprime le cours */
    public void delete(UUID teacherId, UUID courseId) {
        try {
            session.execute(deleteStmt.bind(teacherId, courseId));
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la suppression du cours " + courseId, e);
        }
    }

    private Course map(Row row, UUID teacherId) {
        Course c = new Course();
        c.setCourseId(row.getUuid("course_id"));
        c.setTeacherId(teacherId);
        c.setTitle(row.getString("title"));
        c.setDescription(row.getString("description"));
        c.setCreatedAt(row.getInstant("created_at"));
        return c;
    }
}
