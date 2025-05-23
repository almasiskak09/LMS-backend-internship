package kz.bitlab.MainService.repository;

import jakarta.transaction.Transactional;
import kz.bitlab.MainService.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Long> {
}
