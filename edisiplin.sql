CREATE TABLE users (
    user_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    staff_id VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    age INT DEFAULT 0,
    gender VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incidents (
    incident_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    offense_type VARCHAR(50) NOT NULL,
    incident_date DATE NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) DEFAULT 'Not Set',
    assigned_to INT REFERENCES users(user_id),
    appointment_date DATE,
    logged_by INT REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (staff_id, full_name, email, password, user_type, age, gender) VALUES
('HEP001', 'Admin HEP', 'hep@edisiplin.edu', '1234', 'HEP', 30, 'Male'),
('CNS001', 'Counselor One', 'counselor1@edisiplin.edu', '1234', 'Counselor', 28, 'Female');

INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by)
VALUES ('S001', 'Ali Ahmad', 'Bullying', '2026-06-10', 'Bullying incident in the classroom', 'Pending', 2, '2026-06-20', 1);

INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by)
VALUES ('S002', 'Siti Fatimah', 'Cheating', '2026-06-12', 'Cheating during final exam', 'Completed', 2, '2026-06-18', 1);

INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by)
VALUES ('S003', 'Bob Tan', 'Fighting', '2026-06-15', 'Physical fight in the hallway', 'Pending', 2, '2026-06-25', 1);

INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by)
VALUES ('S004', 'Mei Ling', 'Smoking', '2026-06-16', 'Caught smoking in the toilet', 'Not Set', 2, NULL, 1);

INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by)
VALUES ('S005', 'Raj Kumar', 'Skipping Classes', '2026-06-17', 'Skipped 3 classes this week', 'Completed', 2, '2026-06-19', 1);
