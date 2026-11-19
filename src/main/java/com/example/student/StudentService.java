
package com.example.student;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // 1. Calculate average score
    public double calculateAverage(Student student) {
        return (student.getProgrammingScore()
                + student.getMathematicsScore()) / 2.0;
    }

    // 2. Performance category
    public String getPerformanceCategory(Student student) {
        double average = calculateAverage(student);

        if (average >= 85) {
            return "Excellent";
        } else if (average >= 70) {
            return "Good";
        } else if (average >= 50) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    // 3. Identify stronger subject
    public String getStrongerSubject(Student student) {
        if (student.getProgrammingScore()
                > student.getMathematicsScore()) {
            return "Programming";
        } else if (student.getMathematicsScore()
                > student.getProgrammingScore()) {
            return "Mathematics";
        } else {
            return "Both subjects are equal";
        }
    }

    // 4. Search by ID
    public Student searchById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // Search by email
    public List<Student> searchByEmail(String email) {
        return repository.findByEmail(email);
    }

    // Search by course
    public List<Student> searchByCourse(String course) {
        return repository.findByCourseName(course);
    }

    // 5. Merit list: highest average first
    public List<Student> getMeritList() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparingDouble(
                        this::calculateAverage).reversed())
                .collect(Collectors.toList());
    }

    // 6. Students having at least one common technical skill
    public List<Student> findStudentsWithSameSkills() {
        List<Student> students = repository.findAll();

        return students.stream()
                .filter(student -> students.stream()
                        .anyMatch(other ->
                                !student.getStudentId().equals(
                                        other.getStudentId())
                                && student.getTechnicalSkills().stream()
                                        .anyMatch(other.getTechnicalSkills()::contains)))
                .collect(Collectors.toList());
    }

    // 7. Programming below 50 and Mathematics at least 75
    public List<Student> getStudentsNeedingProgrammingImprovement() {
        return repository.findAll().stream()
                .filter(student ->
                        student.getProgrammingScore() < 50
                        && student.getMathematicsScore() >= 75)
                .collect(Collectors.toList());
    }

    // 8. Validate scores
    public void validateScores(Student student) {
        if (student.getProgrammingScore() < 0
                || student.getProgrammingScore() > 100
                || student.getMathematicsScore() < 0
                || student.getMathematicsScore() > 100) {
            throw new IllegalArgumentException(
                    "Scores must be between 0 and 100");
        }
    }

    // Add student and prevent duplicate ID
    public Student addStudent(Student student) {
        validateScores(student);

        if (student.getStudentId() == null
                || repository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException(
                    "Student ID is missing or already exists");
        }

        return repository.save(student);
    }

    // Update existing student
    public Student addUpdatedStudent(Student student) {
        validateScores(student);
        return repository.save(student);
    }

    // Delete student
    public void deleteStudent(Long id) {
        repository.deleteById(id);
    }
}
