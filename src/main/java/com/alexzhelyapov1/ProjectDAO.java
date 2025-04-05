package com.alexzhelyapov1;

// ProjectDAO.java
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT id, name, tags, deadline FROM projects";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setName(rs.getString("name"));
                project.setTags(rs.getString("tags"));
                project.setDeadline(rs.getDate("deadline").toLocalDate());
                projects.add(project);
            }
        }
        return projects;
    }

    public void addProject(Project project) throws SQLException {
        String sql = "INSERT INTO projects (name, tags, deadline) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getTags());
            pstmt.setDate(3, Date.valueOf(project.getDeadline()));
            pstmt.executeUpdate();
        }
    }
}
