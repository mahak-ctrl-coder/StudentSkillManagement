
package com.example.student;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByEmail(String email);

    List<Student> findByCourseName(String courseName);
}
