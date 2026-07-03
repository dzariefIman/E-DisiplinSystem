package model;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String staffID;
    private String fullName;
    private String email;
    private String password;
    private String userType;
    private int age;
    private String gender;

    public User() {}

    public User(String staffID, String fullName, String email, String password, String userType, int age, String gender) {
        this.staffID = staffID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.age = age;
        this.gender = gender;
    }

    public static User login(String staffID, String password) {
        String sql = "SELECT * FROM users WHERE staffID = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffID);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean register(User user) {
        String sql = "INSERT INTO users (staffID, full_name, email, password, user_type, age, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.staffID);
            ps.setString(2, user.fullName);
            ps.setString(3, user.email);
            ps.setString(4, user.password);
            ps.setString(5, user.userType);
            ps.setInt(6, user.age);
            ps.setString(7, user.gender);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getAllCounselors() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_type = 'Counselor' ORDER BY full_name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static User findByStaffId(String staffID) {
        String sql = "SELECT * FROM users WHERE staffID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean existsByStaffId(String staffID) {
        String sql = "SELECT COUNT(*) FROM users WHERE staffID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.staffID = rs.getString("staffID");
        u.fullName = rs.getString("full_name");
        u.email = rs.getString("email");
        u.password = rs.getString("password");
        u.userType = rs.getString("user_type");
        u.age = rs.getInt("age");
        u.gender = rs.getString("gender");
        return u;
    }

    public String getStaffID() { return staffID; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }
    public int getAge() { return age; }
    public String getGender() { return gender; }

    public void setStaffID(String staffID) { this.staffID = staffID; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
}
