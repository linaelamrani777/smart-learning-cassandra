-- Création du Keyspace
CREATE KEYSPACE IF NOT EXISTS smart_learning 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE smart_learning;

-- R1: Lister les cours créés par un enseignant
CREATE TABLE IF NOT EXISTS courses_by_teacher (
    teacher_id uuid,
    course_id uuid,
    title text,
    description text,
    created_at timestamp,
    PRIMARY KEY ((teacher_id), course_id)
);

-- R3: Lister les cours auxquels un étudiant est inscrit
CREATE TABLE IF NOT EXISTS courses_by_student (
    student_id uuid,
    course_id uuid,
    course_title text,
    teacher_id uuid,
    enrolled_at timestamp,
    PRIMARY KEY ((student_id), course_id)
);

-- R2: Lister les étudiants inscrits à un cours (Dénormalisation)
CREATE TABLE IF NOT EXISTS students_by_course (
    course_id uuid,
    student_id uuid,
    student_name text,
    email text,
    enrolled_at timestamp,
    PRIMARY KEY ((course_id), student_id)
);

-- R4: Afficher les devoirs d'un cours
CREATE TABLE IF NOT EXISTS assignments_by_course (
    course_id uuid,
    assignment_id uuid,
    title text,
    description text,
    due_date timestamp,
    PRIMARY KEY ((course_id), assignment_id)
);

-- R5: Consulter les soumissions (travaux rendus) d'un devoir
CREATE TABLE IF NOT EXISTS submissions_by_assignment (
    assignment_id uuid,
    submission_id uuid,
    student_id uuid,
    content text,
    submitted_at timestamp,
    grade double,
    feedback text,
    PRIMARY KEY ((assignment_id), submission_id)
);

-- R6: Consulter les notes et feedbacks d'un étudiant
CREATE TABLE IF NOT EXISTS grades_by_student (
    student_id uuid,
    course_id uuid,
    assignment_id uuid,
    grade double,
    feedback text,
    graded_at timestamp,
    PRIMARY KEY ((student_id), course_id, assignment_id)
);

-- R7: Enregistrer et suivre l'activité des utilisateurs (avec tri par date/heure décroissante)
CREATE TABLE IF NOT EXISTS activities_by_user (
    user_id uuid,
    activity_date date,
    activity_id timeuuid,
    action_type text,
    details text,
    PRIMARY KEY ((user_id, activity_date), activity_id)
) WITH CLUSTERING ORDER BY (activity_id DESC);