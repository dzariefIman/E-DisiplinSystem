package model;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CounselingSession {

    private String sessionId;
    private String caseId;
    private String staffID;
    private Date appointmentDate;
    private String status;

    private DisciplinaryCase disciplinaryCase;
    private User counselor;

    public CounselingSession() {}

    public static CounselingSession getById(String sessionId) {
        String sql = "SELECT * FROM counseling_session WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSession(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CounselingSession getByCaseId(String caseId) {
        String sql = "SELECT * FROM counseling_session WHERE case_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, caseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSession(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CounselingSession> getByCounselor(String counselorStaffId) {
        List<CounselingSession> list = new ArrayList<>();
        String sql = "SELECT * FROM counseling_session WHERE staffID = ? ORDER BY appointment_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CounselingSession> getByCounselorAndDate(String counselorStaffId, Date date) {
        List<CounselingSession> list = new ArrayList<>();
        String sql = "SELECT * FROM counseling_session WHERE staffID = ? AND appointment_date = ? ORDER BY session_id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getCompletedCount(String counselorStaffId) {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE staffID = ? AND status = 'Completed'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getPendingCount(String counselorStaffId) {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE staffID = ? AND status = 'Pending'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getNotSetCount(String counselorStaffId) {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE staffID = ? AND status = 'Not Set'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getTotalCount(String counselorStaffId) {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE staffID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getGlobalPendingCount() {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE status = 'Pending'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getGlobalNotSetCount() {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE status = 'Not Set'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getGlobalCompletedCount() {
        String sql = "SELECT COUNT(*) FROM counseling_session WHERE status = 'Completed'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static List<CounselingSession> getLatestByCounselor(String counselorStaffId, int limit) {
        List<CounselingSession> list = new ArrayList<>();
        String sql = "SELECT cs.* FROM counseling_session cs JOIN disciplinary_case dc ON cs.case_id = dc.case_id WHERE cs.staffID = ? ORDER BY dc.incident_date DESC FETCH FIRST ? ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, counselorStaffId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean add() {
        String sql = "INSERT INTO counseling_session (session_id, case_id, staffID, appointment_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.sessionId);
            ps.setString(2, this.caseId);
            ps.setString(3, this.staffID);
            if (this.appointmentDate != null) ps.setDate(4, this.appointmentDate);
            else ps.setNull(4, Types.DATE);
            ps.setString(5, this.status != null ? this.status : "Not Set");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setAppointment(String sessionId, Date appointmentDate) {
        String sql = "UPDATE counseling_session SET appointment_date = ?, status = 'Pending' WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, appointmentDate);
            ps.setString(2, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDate(String sessionId, Date appointmentDate) {
        String sql = "UPDATE counseling_session SET appointment_date = ? WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, appointmentDate);
            ps.setString(2, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reopen(String sessionId) {
        String sql = "UPDATE counseling_session SET status = 'Pending' WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean markComplete(String sessionId) {
        String sql = "UPDATE counseling_session SET status = 'Completed' WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNextSessionId() {
        String sql = "SELECT session_id FROM counseling_session ORDER BY session_id DESC FETCH FIRST 1 ROW ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String last = rs.getString("session_id");
                int num = Integer.parseInt(last.replace("SESS", "")) + 1;
                return String.format("SESS%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SESS001";
    }

    private static CounselingSession mapSession(ResultSet rs) throws SQLException {
        CounselingSession s = new CounselingSession();
        s.sessionId = rs.getString("session_id");
        s.caseId = rs.getString("case_id");
        s.staffID = rs.getString("staffID");
        s.appointmentDate = rs.getDate("appointment_date");
        s.status = rs.getString("status");
        return s;
    }

    public DisciplinaryCase getDisciplinaryCase() {
        if (disciplinaryCase == null && caseId != null) disciplinaryCase = DisciplinaryCase.getById(caseId);
        return disciplinaryCase;
    }

    public User getCounselor() {
        if (counselor == null && staffID != null) counselor = User.findByStaffId(staffID);
        return counselor;
    }

    public String getCounselorName() {
        User u = getCounselor();
        return u != null ? u.getFullName() : "N/A";
    }

    public String getAppointmentDateDisplay() {
        return appointmentDate != null ? String.format("%td-%tm-%tY", appointmentDate, appointmentDate, appointmentDate) : "Not Set";
    }

    public String getStatusClass() {
        if ("Completed".equals(status)) return "status-completed";
        if ("Not Set".equals(status)) return "status-notset";
        return "status-pending";
    }

    public String getSessionId() { return sessionId; }
    public String getCaseId() { return caseId; }
    public String getStaffID() { return staffID; }
    public Date getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }
    public void setStaffID(String staffID) { this.staffID = staffID; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setStatus(String status) { this.status = status; }

    public boolean update() {
        String sql = "UPDATE counseling_session SET staffID = ? WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.staffID);
            ps.setString(2, this.sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
