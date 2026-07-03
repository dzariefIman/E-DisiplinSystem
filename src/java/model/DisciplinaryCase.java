package model;

import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaryCase {

    private String caseId;
    private String studentId;
    private String offenseType;
    private String description;
    private Date incidentDate;
    private String staffID;

    private Student student;
    private User loggedByUser;
    private CounselingSession counselingSession;

    public DisciplinaryCase() {}

    public static DisciplinaryCase getById(String caseId) {
        String sql = "SELECT * FROM disciplinary_case WHERE case_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, caseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCase(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<DisciplinaryCase> getAll() {
        List<DisciplinaryCase> list = new ArrayList<>();
        String sql = "SELECT * FROM disciplinary_case ORDER BY incident_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCase(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<DisciplinaryCase> search(String keyword, String offenseType, String status, String counselorStaffId) {
        StringBuilder sql = new StringBuilder(
            "SELECT dc.* FROM disciplinary_case dc " +
            "JOIN student s ON dc.student_id = s.student_id " +
            "JOIN counseling_session cs ON dc.case_id = cs.case_id " +
            "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(s.student_name) LIKE ? OR LOWER(dc.student_id) LIKE ?)");
            params.add("%" + keyword.toLowerCase() + "%");
            params.add("%" + keyword.toLowerCase() + "%");
        }
        if (offenseType != null && !offenseType.isEmpty()) {
            sql.append(" AND dc.offense_type = ?");
            params.add(offenseType);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND cs.status = ?");
            params.add(status);
        }
        if (counselorStaffId != null && !counselorStaffId.isEmpty()) {
            sql.append(" AND cs.staffID = ?");
            params.add(counselorStaffId);
        }
        sql.append(" ORDER BY dc.incident_date DESC");

        List<DisciplinaryCase> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCase(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getTotalCases() {
        String sql = "SELECT COUNT(*) FROM disciplinary_case";
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
        String sql = "SELECT offense_type, COUNT(*) AS cnt FROM disciplinary_case GROUP BY offense_type ORDER BY cnt DESC FETCH FIRST 1 ROW ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getString("offense_type") + " (" + rs.getInt("cnt") + ")";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public boolean add() {
        String sql = "INSERT INTO disciplinary_case (case_id, student_id, offense_type, description, incident_date, staffID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.caseId);
            ps.setString(2, this.studentId);
            ps.setString(3, this.offenseType);
            ps.setString(4, this.description);
            ps.setDate(5, this.incidentDate);
            ps.setString(6, this.staffID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update() {
        String sql = "UPDATE disciplinary_case SET student_id=?, offense_type=?, description=?, incident_date=? WHERE case_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.studentId);
            ps.setString(2, this.offenseType);
            ps.setString(3, this.description);
            ps.setDate(4, this.incidentDate);
            ps.setString(5, this.caseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String caseId) {
        String sql1 = "DELETE FROM counseling_session WHERE case_id = ?";
        String sql2 = "DELETE FROM disciplinary_case WHERE case_id = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, caseId);
            ps1.executeUpdate();
            ps1.close();
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, caseId);
            boolean result = ps2.executeUpdate() > 0;
            ps2.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNextCaseId() {
        String sql = "SELECT case_id FROM disciplinary_case ORDER BY case_id DESC FETCH FIRST 1 ROW ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String last = rs.getString("case_id");
                int num = Integer.parseInt(last.replace("CASE", "")) + 1;
                return String.format("CASE%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "CASE001";
    }

    private static DisciplinaryCase mapCase(ResultSet rs) throws SQLException {
        DisciplinaryCase c = new DisciplinaryCase();
        c.caseId = rs.getString("case_id");
        c.studentId = rs.getString("student_id");
        c.offenseType = rs.getString("offense_type");
        c.description = rs.getString("description");
        c.incidentDate = rs.getDate("incident_date");
        c.staffID = rs.getString("staffID");
        return c;
    }

    public Student getStudent() {
        if (student == null && studentId != null) student = Student.findById(studentId);
        return student;
    }

    public User getLoggedByUser() {
        if (loggedByUser == null && staffID != null) loggedByUser = User.findByStaffId(staffID);
        return loggedByUser;
    }

    public String getStudentName() {
        Student s = getStudent();
        return s != null ? s.getStudentName() : "";
    }

    public String getIncidentDateDisplay() {
        if (incidentDate == null) return "";
        return String.format("%td%tm%ty", incidentDate, incidentDate, incidentDate);
    }

    public String getLoggedByName() {
        User u = getLoggedByUser();
        return u != null ? u.getFullName() : "N/A";
    }

    public CounselingSession getCounselingSession() {
        if (counselingSession == null && caseId != null) counselingSession = CounselingSession.getByCaseId(caseId);
        return counselingSession;
    }

    public String getStatus() {
        CounselingSession s = getCounselingSession();
        return s != null ? s.getStatus() : "Not Set";
    }

    public String getAppointmentDateDisplay() {
        CounselingSession s = getCounselingSession();
        return s != null ? s.getAppointmentDateDisplay() : "Not Set";
    }

    public String getAssignedCounselorName() {
        CounselingSession s = getCounselingSession();
        return s != null ? s.getCounselorName() : "N/A";
    }

    public String getStatusClass() {
        return getStatus().equals("Completed") ? "status-completed" : getStatus().equals("Not Set") ? "status-notset" : "status-pending";
    }

    public String getCaseId() { return caseId; }
    public String getStudentId() { return studentId; }
    public String getOffenseType() { return offenseType; }
    public String getDescription() { return description; }
    public Date getIncidentDate() { return incidentDate; }
    public String getStaffID() { return staffID; }

    public void setCaseId(String caseId) { this.caseId = caseId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setOffenseType(String offenseType) { this.offenseType = offenseType; }
    public void setDescription(String description) { this.description = description; }
    public void setIncidentDate(Date incidentDate) { this.incidentDate = incidentDate; }
    public void setStaffID(String staffID) { this.staffID = staffID; }
}
