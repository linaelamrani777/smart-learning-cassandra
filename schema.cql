CREATE KEYSPACE IF NOT EXISTS smart_learning WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE smart_learning;

CREATE TABLE IF NOT EXISTS courses_by_teacher (teacher_id uuid, course_id uuid, title text, description text, created_at timestamp, PRIMARY KEY (teacher_id, course_id));
CREATE TABLE IF NOT EXISTS students_by_course (course_id uuid, student_id uuid, student_name text, student_email text, enrolled_at timestamp, PRIMARY KEY (course_id, student_id));
CREATE TABLE IF NOT EXISTS courses_by_student (student_id uuid, course_id uuid, course_title text, teacher_name text, enrolled_at timestamp, PRIMARY KEY (student_id, course_id));
CREATE TABLE IF NOT EXISTS assignments_by_course (course_id uuid, assignment_id uuid, title text, description text, due_date timestamp, PRIMARY KEY (course_id, assignment_id));
CREATE TABLE IF NOT EXISTS submissions_by_assignment (assignment_id uuid, submission_id uuid, student_id uuid, student_name text, file_url text, submitted_at timestamp, grade double, feedback text, PRIMARY KEY (assignment_id, submission_id));
CREATE TABLE IF NOT EXISTS grades_by_student (student_id uuid, submission_id uuid, course_id uuid, assignment_title text, grade double, feedback text, graded_at timestamp, PRIMARY KEY (student_id, submission_id));
CREATE TABLE IF NOT EXISTS activity_logs_by_user (user_id uuid, activity_timestamp timestamp, activity_id uuid, action_type text, details text, PRIMARY KEY (user_id, activity_timestamp, activity_id)) WITH CLUSTERING ORDER BY (activity_timestamp DESC, activity_id ASC);
