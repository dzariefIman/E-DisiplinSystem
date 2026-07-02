package model;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Incident {

    private int incidentId;
    private String studentId;
    private String studentName;
    private String offenseType;
    private Date incidentDate;
    private String description;
    private String status;
    private int assignedTo;
    private Date appointmentDate;
    private int loggedBy;
    private Timestamp createdAt;

    private User assignedCounselor;
    private User loggedByUser;

    public Incident() {}

    public static List<Incident> getAll() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents ORDER BY incident_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapIncident(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Incident> getByCounselor(int counselorId) {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_to = ? ORDER BY incident_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapIncident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Incident getById(int incidentId) {
        String sql = "SELECT * FROM incidents WHERE incident_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapIncident(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Incident> search(String keyword, String offenseType, String status, Integer counselorId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM incidents WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(student_name) LIKE ? OR LOWER(student_id) LIKE ?)");
            params.add("%" + keyword.toLowerCase() + "%");
            params.add("%" + keyword.toLowerCase() + "%");
        }
        if (offenseType != null && !offenseType.isEmpty()) {
            sql.append(" AND offense_type = ?");
            params.add(offenseType);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (counselorId != null) {
            sql.append(" AND assigned_to = ?");
            params.add(counselorId);
        }
        sql.append(" ORDER BY incident_date DESC");

        List<Incident> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapIncident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getTotalCases() {
        String sql = "SELECT COUNT(*) FROM incidents";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getMostCommonOffense() {
        String sql = "SELECT offense_type, COUNT(*) AS cnt FROM incidents GROUP BY offense_type ORDER BY cnt DESC FETCH FIRST 1 ROW ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getString("offense_type") + " (" + rs.getInt("cnt") + ")";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public static int getPendingCount() {
        String sql = "SELECT COUNT(*) FROM incidents WHERE status = 'Pending'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNotSetCount() {
        String sql = "SELECT COUNT(*) FROM incidents WHERE status = 'Not Set'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCompletedByCounselor(int counselorId) {
        String sql = "SELECT COUNT(*) FROM incidents WHERE assigned_to = ? AND status = 'Completed'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPendingByCounselor(int counselorId) {
        String sql = "SELECT COUNT(*) FROM incidents WHERE assigned_to = ? AND status = 'Pending'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNotSetByCounselor(int counselorId) {
        String sql = "SELECT COUNT(*) FROM incidents WHERE assigned_to = ? AND status = 'Not Set'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalByCounselor(int counselorId) {
        String sql = "SELECT COUNT(*) FROM incidents WHERE assigned_to = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean add() {
        String sql = "INSERT INTO incidents (student_id, student_name, offense_type, incident_date, description, status, assigned_to, appointment_date, logged_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.studentId);
            ps.setString(2, this.studentName);
            ps.setString(3, this.offenseType);
            ps.setDate(4, this.incidentDate);
            ps.setString(5, this.description);
            ps.setString(6, this.status != null ? this.status : "Not Set");
            ps.setInt(7, this.assignedTo);
            if (this.appointmentDate != null) ps.setDate(8, this.appointmentDate);
            else ps.setNull(8, Types.DATE);
            ps.setInt(9, this.loggedBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update() {
        String sql = "UPDATE incidents SET student_id=?, student_name=?, offense_type=?, incident_date=?, description=?, status=?, assigned_to=?, appointment_date=? WHERE incident_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.studentId);
            ps.setString(2, this.studentName);
            ps.setString(3, this.offenseType);
            ps.setDate(4, this.incidentDate);
            ps.setString(5, this.description);
            ps.setString(6, this.status);
            ps.setInt(7, this.assignedTo);
            if (this.appointmentDate != null) ps.setDate(8, this.appointmentDate);
            else ps.setNull(8, Types.DATE);
            ps.setInt(9, this.incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(int incidentId) {
        String sql = "DELETE FROM incidents WHERE incident_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateStatus(int incidentId, String status) {
        String sql = "UPDATE incidents SET status = ? WHERE incident_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setAppointment(int incidentId, Date appointmentDate) {
        String sql = "UPDATE incidents SET appointment_date = ?, status = 'Pending' WHERE incident_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, appointmentDate);
            ps.setInt(2, incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Incident> getByCounselorAndDate(int counselorId, Date date) {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_to = ? AND appointment_date = ? ORDER BY student_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapIncident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Incident> getLatestByCounselor(int counselorId, int limit) {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_to = ? ORDER BY incident_date DESC FETCH FIRST ? ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, counselorId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapIncident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Incident mapIncident(ResultSet rs) throws SQLException {
        Incident i = new Incident();
        i.incidentId = rs.getInt("incident_id");
        i.studentId = rs.getString("student_id");
        i.studentName = rs.getString("student_name");
        i.offenseType = rs.getString("offense_type");
        i.incidentDate = rs.getDate("incident_date");
        i.description = rs.getString("description");
        i.status = rs.getString("status");
        i.assignedTo = rs.getInt("assigned_to");
        i.appointmentDate = rs.getDate("appointment_date");
        i.loggedBy = rs.getInt("logged_by");
        i.createdAt = rs.getTimestamp("created_at");
        return i;
    }

    public int getIncidentId() { return incidentId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getOffenseType() { return offenseType; }
    public Date getIncidentDate() { return incidentDate; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public int getAssignedTo() { return assignedTo; }
    public Date getAppointmentDate() { return appointmentDate; }
    public int getLoggedBy() { return loggedBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    
    public void setIncidentId(int incidentId) { this.incidentId = incidentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setOffenseType(String offenseType) { this.offenseType = offenseType; }
    public void setIncidentDate(Date incidentDate) { this.incidentDate = incidentDate; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setLoggedBy(int loggedBy) { this.loggedBy = loggedBy; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public User getAssignedCounselor() {
        if (assignedCounselor == null && assignedTo > 0) assignedCounselor = User.findById(assignedTo);
        return assignedCounselor;
    }
    
    public User getLoggedByUser() {
        if (loggedByUser == null && loggedBy > 0) loggedByUser = User.findById(loggedBy);
        return loggedByUser;
    }
    
    public String getAssignedCounselorName() {
        User u = getAssignedCounselor();
        return u != null ? u.getFullName() : "N/A";
    }
    
    public String getIncidentDateDisplay() {
        if (incidentDate == null) return "";
        return String.format("%td%tm%ty", incidentDate, incidentDate, incidentDate);
    }

    public String getAppointmentDateDisplay() {
        return appointmentDate != null ? String.format("%td%tm%ty", appointmentDate, appointmentDate, appointmentDate) : "Not Set";
    }
    
    public String getStatusClass() {
        if ("Completed".equals(status)) return "status-completed";
        if ("Not Set".equals(status)) return "status-notset";
        return "status-pending";
    }
}
