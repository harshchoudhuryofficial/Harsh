package com.harsh.studentmgmt.model;

public record Student(
        Long id,
        String rollNo,
        String fullName,
        String email,
        String department,
        double cgpa
) {
}
