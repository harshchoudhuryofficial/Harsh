package com.harsh.studentmgmt.model;

public record Course(
        Long id,
        String code,
        String title,
        int credits
) {
}
