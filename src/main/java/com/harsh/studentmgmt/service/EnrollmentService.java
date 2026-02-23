package com.harsh.studentmgmt.service;

import com.harsh.studentmgmt.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollmentService {

    public void enrollStudent(String rollNo, String courseCode, String semester) {
        String findStudent = "SELECT id FROM students WHERE roll_no = ?";
        String findCourse = "SELECT id FROM courses WHERE code = ?";
        String enroll = "INSERT INTO enrollments (student_id, course_id, semester) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Long studentId = fetchId(connection, findStudent, rollNo, "student");
                Long courseId = fetchId(connection, findCourse, courseCode, "course");

                try (PreparedStatement stmt = connection.prepareStatement(enroll)) {
                    stmt.setLong(1, studentId);
                    stmt.setLong(2, courseId);
                    stmt.setString(3, semester);
                    stmt.executeUpdate();
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enroll student", e);
        }
    }

    public int countEnrollments(String rollNo) {
        String sql = """
                SELECT COUNT(*) AS enrollments_count
                FROM enrollments e
                JOIN students s ON s.id = e.student_id
                WHERE s.roll_no = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rollNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("enrollments_count");
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count enrollments", e);
        }
    }

    private Long fetchId(Connection connection, String sql, String lookup, String entityName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lookup);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new IllegalArgumentException("No " + entityName + " found for value: " + lookup);
    }
}
