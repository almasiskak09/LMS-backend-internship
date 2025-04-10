package kz.bitlab.MainService.controllerTestIT;

import jakarta.transaction.Transactional;
import kz.bitlab.MainService.AbstractTestIntegrationIT;
import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.repository.CourseRepository;
import kz.bitlab.MainService.restApi.CourseController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"classpath:/db/test/V1__insert.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:/db/test/clean.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
public class CourseControllerTestIT extends AbstractTestIntegrationIT {

    @Autowired
    private CourseController courseController;

    @Autowired
    private CourseRepository courseRepository;

    private LocalDateTime date;
    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024, 4, 7, 12, 0);
    }

    @Test
    public void getAllCourses(){
        ResponseEntity<List<CourseDto>> response = courseController.getAllCourses();
        List<CourseDto> courses = response.getBody();

       assertNotNull(courses);
       assertTrue(courses.size() >= 5);
       assertEquals("Java Developer",courses.get(0).getCourseName());
       assertEquals("Learn Python programming",courses.get(1).getCourseDescription());
       assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getCourseById(){
        Long findId = 5L;
        ResponseEntity<CourseDto> response = courseController.getCourseById(findId);
        CourseDto course = response.getBody();

        assertNotNull(course);
        assertEquals(findId, course.getId());
        assertEquals("Data Scientist",course.getCourseName());
        assertEquals("Learn data science techniques",course.getCourseDescription());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void createCourse() {

        Long findId = 6L;

        CourseDto courseDto = new CourseDto(null, "QA Dev", "Programming", date, date, null);
        ResponseEntity<CourseDto> response = courseController.addCourse(courseDto);

        Course course = courseRepository.findById(findId).orElseThrow(null);

        assertEquals("QA Dev",course.getCourseName());
        assertEquals("Programming",course.getCourseDescription());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    public void updateCourse(){

        CourseDto updateCourse = new CourseDto(1L,"Python Developer","Programming",date,date,null);
        ResponseEntity<CourseDto> response = courseController.updateCourse(updateCourse);

        Course course = courseRepository.findById(1L).orElseThrow(null);

        assertEquals("Python Developer",course.getCourseName());
        assertEquals("Programming",course.getCourseDescription());
        assertEquals(HttpStatus.OK, response.getStatusCode());



    }

    @Test
    public void deleteCourseById(){
        Long findId = 1L;
        ResponseEntity<Void> response = courseController.deleteCourseById(findId);
        assertFalse(courseRepository.findById(findId).isPresent());

    }

}
