# Smart Learning Platform - Apache Cassandra Database Architecture

## Overview
This project uses **Apache Cassandra 5.0** deployed via Docker. The data model is fully denormalized to support query-first design for an online learning management system.

## Environment & Connection
- **Container Name:** `cassandra-v5`
- **Host:** `localhost` (`127.0.0.1`)
- **Port:** `9042`
- **Keyspace:** `smart_learning` (SimpleStrategy, Replication Factor: 1)
- **Local Datacenter:** `datacenter1`

## Data Model & Query Patterns (7 Tables)

1. `courses_by_teacher`: Primary Key (`teacher_id`, `course_id`)
   - Retrieves all courses managed by a specific teacher.
2. `students_by_course`: Primary Key (`course_id`, `student_id`)
   - Retrieves enrolled students for a given course.
3. `courses_by_student`: Primary Key (`student_id`, `course_id`)
   - Displays a student's enrolled course catalog.
4. `assignments_by_course`: Primary Key (`course_id`, `assignment_id`)
   - Lists assignments published under a course.
5. `submissions_by_assignment`: Primary Key (`assignment_id`, `submission_id`)
   - Stores student submissions and grades for a specific assignment.
6. `grades_by_student`: Primary Key (`student_id`, `submission_id`)
   - Shows all grades and feedback given to a student.
7. `activity_logs_by_user`: Primary Key (`user_id`, `activity_timestamp`, `activity_id`)
   - Tracks audit logs sorted newest first using `CLUSTERING ORDER BY (activity_timestamp DESC)`.

## Execution Commands
To recreate the schema and load sample data into Docker:
```cmd
docker cp src/main/resources/cql/schema.cql cassandra-v5:/tmp/schema.cql
docker cp src/main/resources/cql/seed.cql cassandra-v5:/tmp/seed.cql

docker exec -i cassandra-v5 cqlsh -f /tmp/schema.cql
docker exec -i cassandra-v5 cqlsh -f /tmp/seed.cql