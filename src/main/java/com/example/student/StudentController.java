
package com.example.student;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // 1. Add a new student
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student savedStudent = service.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getMeritList().stream()
                .sorted((a, b) -> a.getStudentId()
                        .compareTo(b.getStudentId()))
                .toList();
    }

    // 3. Search student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id) {

        Student student = service.searchById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    // 4. Update scores and skills
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @RequestBody Student updatedStudent) {

        Student existingStudent = service.searchById(id);

        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            service.validateScores(updatedStudent);

            existingStudent.setProgrammingScore(
                    updatedStudent.getProgrammingScore());

            existingStudent.setMathematicsScore(
                    updatedStudent.getMathematicsScore());

            existingStudent.setTechnicalSkills(
                    updatedStudent.getTechnicalSkills());

            return ResponseEntity.ok(
                    service.addUpdatedStudent(existingStudent));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {

        Student student = service.searchById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        service.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully");
    }

    // 6. Display merit list
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return service.getMeritList();
    }

    // 7. Search by email
    @GetMapping("/search/email")
    public List<Student> searchByEmail(@RequestParam String email) {
        return service.searchByEmail(email);
    }

    // 8. Search by course
    @GetMapping("/search/course")
    public List<Student> searchByCourse(@RequestParam String course) {
        return service.searchByCourse(course);
    }

    // 9. Students with same technical skills
    @GetMapping("/same-skills")
    public List<Student> sameSkills() {
        return service.findStudentsWithSameSkills();
    }

    // 10. Students needing programming improvement
    @GetMapping("/programming-improvement")
    public List<Student> programmingImprovement() {
        return service.getStudentsNeedingProgrammingImprovement();
    }

    // 11. Average, performance category and stronger subject
    @GetMapping("/{id}/performance")
    public ResponseEntity<?> getStudentPerformance(
            @PathVariable Long id) {

        Student student = service.searchById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "studentName", student.getStudentName(),
                        "average", service.calculateAverage(student),
                        "category", service.getPerformanceCategory(student),
                        "strongerSubject", service.getStrongerSubject(student)
                )
        );
    }
}
