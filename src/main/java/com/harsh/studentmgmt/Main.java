package com.harsh.studentmgmt;

import com.harsh.studentmgmt.dao.CourseDao;
import com.harsh.studentmgmt.dao.StudentDao;
import com.harsh.studentmgmt.model.Course;
import com.harsh.studentmgmt.model.Student;
import com.harsh.studentmgmt.service.EnrollmentService;
import com.harsh.studentmgmt.util.SchemaInitializer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        SchemaInitializer.initialize();

        StudentDao studentDao = new StudentDao();
        CourseDao courseDao = new CourseDao();
        EnrollmentService enrollmentService = new EnrollmentService();

        studentDao.batchCreate(List.of(
                new Student(null, "CS-001", "Aarav Sharma", "aarav@uni.edu", "CSE", 8.75),
                new Student(null, "CS-002", "Diya Mehta", "diya@uni.edu", "CSE", 9.22),
                new Student(null, "EC-001", "Kunal Verma", "kunal@uni.edu", "ECE", 8.43)
        ));

        Student created = studentDao.create(new Student(
                null,
                "CS-003",
                "Riya Kapoor",
                "riya@uni.edu",
                "CSE",
                8.91
        ));
        System.out.println("Created student: " + created);

        courseDao.create(new Course(null, "DBMS301", "Database Systems", 4));
        courseDao.create(new Course(null, "CN302", "Computer Networks", 3));

        enrollmentService.enrollStudent("CS-001", "DBMS301", "SPRING-2026");
        enrollmentService.enrollStudent("CS-001", "CN302", "SPRING-2026");
        enrollmentService.enrollStudent("CS-003", "DBMS301", "SPRING-2026");

        studentDao.updateCgpa("CS-001", 8.95);

        System.out.println("\nTop CSE students (pagination example):");
        studentDao.findByDepartment("CSE", 2, 0)
                .forEach(System.out::println);

        int count = enrollmentService.countEnrollments("CS-001");
        System.out.println("\nEnrollment count for CS-001: " + count);

        studentDao.findByRollNo("CS-003")
                .ifPresent(student -> System.out.println("\nLookup by roll no: " + student));
    }
}
