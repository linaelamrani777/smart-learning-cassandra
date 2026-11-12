package com.smartlearning.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartlearning.model.Course;
import com.smartlearning.model.Student;

/**
 * DAO des inscriptions. Repond a R2 (students_by_course) et R3 (courses_by_student)
 * Chaque inscription est ecrite dans les 2 tables via un LOGGED BATCH (coherence)
 */
public class EnrollmentDAO {
    private static final String KS = "smart_learning.";

    private final CqlSession session;
    private final PreparedStatement insertStudentsByCourse;
    private final PreparedStatement insertCoursesByStudent;
    private final PreparedStatement selectStudentsByCourse;
    private final PreparedStatement selectCoursesByStudent;
    private final PreparedStatement deleteStudentsByCourse;
    private final PreparedStatement deleteCoursesByStudent;

    public EnrollmentDAO(CqlSession session) {
        this.session = session;
        insertStudentsByCourse = session.prepare("INSERT INTO " + KS
                + "students_by_course (course_id, student_id, student_name, student_email, enrolled_at) "
                + "VALUES (?, ?, ?, ?, ?)");
        insertCoursesByStudent = session.prepare("INSERT INTO " + KS
                + "courses_by_student (student_id, course_id, course_title, teacher_name, enrolled_at) "
                + "VALUES (?, ?, ?, ?, ?)");
        selectStudentsByCourse = session.prepare("SELECT student_id, student_name, student_email, enrolled_at FROM "
                + KS + "students_by_course WHERE course_id = ?");
        selectCoursesByStudent = session.prepare("SELECT course_id, course_title, teacher_name, enrolled_at FROM "
                + KS + "courses_by_student WHERE student_id = ?");
        deleteStudentsByCourse = session.prepare("DELETE FROM " + KS
                + "students_by_course WHERE course_id = ? AND student_id = ?");
        deleteCoursesByStudent = session.prepare("DELETE FROM " + KS
                + "courses_by_student WHERE student_id = ? AND course_id = ?");
    }

    /**
     * Inscrit un etudiant a un cours (double insertion atomique)
     * course.getTeacherName() est copie dans courses_by_student (denormalisation).
     */
    public void enroll(Student student, Course course) {
        try {
            Instant now = Instant.now();
            BatchStatement batch = BatchStatement.newInstance(DefaultBatchType.LOGGED)
                    .add(insertStudentsByCourse.bind(course.getCourseId(), student.getStudentId(),
                            student.getName(), student.getEmail(), now))
                    .add(insertCoursesByStudent.bind(student.getStudentId(), course.getCourseId(),
                            course.getTitle(), course.getTeacherName(), now));
            session.execute(batch);
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de l'inscription", e);
        }
    }

    /** R2 : etudiants inscrits a un cours. */
    public List<Student> findStudentsByCourse(UUID courseId) {
        try {
            List<Student> result = new ArrayList<>();
            for (Row row : session.execute(selectStudentsByCourse.bind(courseId))) {
                Student s = new Student(row.getUuid("student_id"),
                        row.getString("student_name"), row.getString("student_email"));
                s.setEnrolledAt(row.getInstant("enrolled_at"));
                result.add(s);
            }
            return result;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la lecture des etudiants du cours " + courseId, e);
        }
    }

    /** R3 : cours suivis par un etudiant */
    public List<Course> findCoursesByStudent(UUID studentId) {
        try {
            List<Course> result = new ArrayList<>();
            for (Row row : session.execute(selectCoursesByStudent.bind(studentId))) {
                Course c = new Course();
                c.setCourseId(row.getUuid("course_id"));
                c.setTitle(row.getString("course_title"));
                c.setTeacherName(row.getString("teacher_name"));
                result.add(c);
            }
            return result;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la lecture des cours de l'etudiant " + studentId, e);
        }
    }

    /** Desinscription : suppression dans les 2 tables (batch) */
    public void unenroll(UUID studentId, UUID courseId) {
        try {
            BatchStatement batch = BatchStatement.newInstance(DefaultBatchType.LOGGED)
                    .add(deleteStudentsByCourse.bind(courseId, studentId))
                    .add(deleteCoursesByStudent.bind(studentId, courseId));
            session.execute(batch);
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la désinscription", e);
        }
    }
}
