package model;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Student {

    private String studentId;
    private String studentName;

    public Student() {}

    public Student(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public static Student findById(String studentId) {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Student findOrCreate(String studentId, String studentName) {
        Student existing = findById(studentId);
        if (existing != null) return existing;
        Student s = new Student(studentId, studentName);
        s.save();
        return s;
    }

    public boolean save() {
        String sql = "INSERT INTO student (student_id, student_name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.studentId);
            ps.setString(2, this.studentName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY student_name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.studentId = rs.getString("student_id");
        s.studentName = rs.getString("student_name");
        return s;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }

    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
