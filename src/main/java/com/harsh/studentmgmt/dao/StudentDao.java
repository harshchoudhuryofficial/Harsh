package com.harsh.studentmgmt.dao;

import com.harsh.studentmgmt.config.DatabaseConfig;
import com.harsh.studentmgmt.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao {

    public Student create(Student student) {
        String sql = """
                INSERT INTO students (roll_no, full_name, email, department, cgpa)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.rollNo());
            statement.setString(2, student.fullName());
            statement.setString(3, student.email());
            statement.setString(4, student.department());
            statement.setDouble(5, student.cgpa());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Student(
                            keys.getLong(1),
                            student.rollNo(),
                            student.fullName(),
                            student.email(),
                            student.department(),
                            student.cgpa()
                    );
                }
            }
            throw new SQLException("No generated key returned");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create student", e);
        }
    }

    public void batchCreate(List<Student> students) {
        String sql = """
                INSERT INTO students (roll_no, full_name, email, department, cgpa)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Student student : students) {
                statement.setString(1, student.rollNo());
                statement.setString(2, student.fullName());
                statement.setString(3, student.email());
                statement.setString(4, student.department());
                statement.setDouble(5, student.cgpa());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed batch insert of students", e);
        }
    }

    public Optional<Student> findByRollNo(String rollNo) {
        String sql = "SELECT id, roll_no, full_name, email, department, cgpa FROM students WHERE roll_no = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rollNo);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find student", e);
        }
    }

    public List<Student> findByDepartment(String department, int limit, int offset) {
        String sql = """
                SELECT id, roll_no, full_name, email, department, cgpa
                FROM students
                WHERE department = ?
                ORDER BY cgpa DESC, full_name ASC
                LIMIT ? OFFSET ?
                """;

        List<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, department);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search students", e);
        }
        return students;
    }

    public boolean updateCgpa(String rollNo, double cgpa) {
        String sql = "UPDATE students SET cgpa = ? WHERE roll_no = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, cgpa);
            statement.setString(2, rollNo);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update CGPA", e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getLong("id"),
                rs.getString("roll_no"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("cgpa")
        );
    }
}
