package com.smartlearning.dao;

import com.smartlearning.config.CassandraSession;
import com.smartlearning.model.Course;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDaoTest {
    private static CourseDao courseDao;
    private static UUID teacherId;
    private static UUID courseId;

    @BeforeAll
    public static void setUp() {
        courseDao = new CourseDao();
        teacherId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    public void testCreateAndGetCourse() {
        String title = "Architecture NoSQL Cassandra";
        String description = "Cours avance sur Cassandra et CQL";
        Instant now = Instant.now();

        Course course = new Course();
        course.setTeacherId(teacherId);
        course.setCourseId(courseId);
        course.setTitle(title);
        course.setDescription(description);
        course.setCreatedAt(now);

        courseDao.createCourse(course);

        List<Course> courses = courseDao.getCoursesByTeacher(teacherId);
        assertFalse(courses.isEmpty(), "La liste des cours ne doit pas être vide");
        
        Course found = courses.stream().filter(c -> c.getCourseId().equals(courseId)).findFirst().orElse(null);
        assertNotNull(found, "Le cours créé doit être retrouvé");
        assertEquals(title, found.getTitle());
    }

    @AfterAll
    public static void tearDown() {
        if (courseDao != null && courseId != null && teacherId != null) {
            courseDao.deleteCourse(teacherId, courseId);
        }
        CassandraSession.close();
    }
}