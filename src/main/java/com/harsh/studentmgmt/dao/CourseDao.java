package com.harsh.studentmgmt.dao;

import com.harsh.studentmgmt.config.DatabaseConfig;
import com.harsh.studentmgmt.model.Course;

import java.sql.*;
import java.util.Optional;

public class CourseDao {

    public Course create(Course course) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, course.code());
            statement.setString(2, course.title());
            statement.setInt(3, course.credits());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Course(keys.getLong(1), course.code(), course.title(), course.credits());
                }
            }
            throw new SQLException("No generated key returned for course");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create course", e);
        }
    }

    public Optional<Course> findByCode(String code) {
        String sql = "SELECT id, code, title, credits FROM courses WHERE code = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Course(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits")
                    ));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find course", e);
        }
    }
}
