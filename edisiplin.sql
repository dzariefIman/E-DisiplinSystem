-- E-Disiplin Database Schema (ERD Version)
-- 4 tables: users, student, disciplinary_case, counseling_session

CREATE TABLE users (
    staffID VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    age INTEGER DEFAULT 0,
    gender VARCHAR(10)
);

CREATE TABLE student (
    student_id VARCHAR(20) PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL
);

CREATE TABLE disciplinary_case (
    case_id VARCHAR(20) PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    offense_type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    incident_date DATE NOT NULL,
    staffID VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (staffID) REFERENCES users(staffID)
);

CREATE TABLE counseling_session (
    session_id VARCHAR(20) PRIMARY KEY,
    case_id VARCHAR(20) NOT NULL,
    staffID VARCHAR(20) NOT NULL,
    appointment_date DATE,
    status VARCHAR(20) DEFAULT 'Not Set',
    FOREIGN KEY (case_id) REFERENCES disciplinary_case(case_id),
    FOREIGN KEY (staffID) REFERENCES users(staffID)
);

-- Seed: Users
INSERT INTO users (staffID, full_name, email, password, user_type, age, gender) VALUES
('HEP001', 'Admin HEP', 'hep@edisiplin.edu', '1234', 'HEP', 30, 'Male'),
('CNS001', 'Dr. Siti Rahman', 'counselor1@edisiplin.edu', '1234', 'Counselor', 35, 'Female'),
('CNS002', 'Mr. Ahmad Faizal', 'counselor2@edisiplin.edu', '1234', 'Counselor', 40, 'Male');

-- Seed: Students
INSERT INTO student (student_id, student_name) VALUES
('S001', 'Ali Ahmad'),
('S002', 'Siti Fatimah'),
('S003', 'Bob Tan'),
('S004', 'Mei Ling'),
('S005', 'Raj Kumar');

-- Seed: Disciplinary Cases
INSERT INTO disciplinary_case (case_id, student_id, offense_type, description, incident_date, staffID) VALUES
('CASE001', 'S001', 'Bullying', 'Bullying incident in the classroom', '2026-06-10', 'HEP001'),
('CASE002', 'S002', 'Cheating', 'Cheating during final exam', '2026-06-12', 'HEP001'),
('CASE003', 'S003', 'Fighting', 'Physical fight in the hallway', '2026-06-15', 'HEP001'),
('CASE004', 'S004', 'Smoking', 'Caught smoking in the toilet', '2026-06-16', 'HEP001'),
('CASE005', 'S005', 'Skipping Classes', 'Skipped 3 classes this week', '2026-06-17', 'HEP001');

-- Seed: Counseling Sessions
INSERT INTO counseling_session (session_id, case_id, staffID, appointment_date, status) VALUES
('SESS001', 'CASE001', 'CNS001', '2026-06-20', 'Pending'),
('SESS002', 'CASE002', 'CNS001', '2026-06-18', 'Completed'),
('SESS003', 'CASE003', 'CNS001', '2026-06-25', 'Pending'),
('SESS004', 'CASE004', 'CNS001', NULL, 'Not Set'),
('SESS005', 'CASE005', 'CNS002', '2026-06-19', 'Completed');
