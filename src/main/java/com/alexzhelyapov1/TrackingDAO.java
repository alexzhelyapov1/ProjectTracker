package com.alexzhelyapov1;

// TrackingDAO.java
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TrackingDAO {
    public void updateStatus(int projectId, LocalDate date, boolean done) throws SQLException {
        String sql = "INSERT OR REPLACE INTO tracking (project_id, date, done) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setBoolean(3, done);
            pstmt.executeUpdate();
        }
    }

    public Map<LocalDate, Map<Integer, Boolean>> getAllStatuses() throws SQLException {
        Map<LocalDate, Map<Integer, Boolean>> allStatuses = new HashMap<>();
        String sql = "SELECT project_id, date, done FROM tracking";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                int projectId = rs.getInt("project_id");
                boolean done = rs.getBoolean("done");

                allStatuses
                        .computeIfAbsent(date, k -> new HashMap<>())
                        .put(projectId, done);
            }
        }
        return allStatuses;
    }
}