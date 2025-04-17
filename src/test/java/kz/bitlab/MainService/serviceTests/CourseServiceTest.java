package kz.bitlab.MainService.serviceTests;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.CourseMapper;
import kz.bitlab.MainService.repository.CourseRepository;
import kz.bitlab.MainService.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    private Course course;
    private CourseDto courseDto;
    private LocalDateTime date;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024, 4, 7, 12, 0);
        course = new Course(1L, "Java", "1J", date, date, null);
        courseDto = new CourseDto(1L, "Java", "1J", date, date, null);
    }

    //Тест на Получение списка всех курсов
    @Test
    void getAllCourses() {

        List<Course> courseList = List.of(
                new Course(1L, "Java", "1J", date, date, null),
                new Course(2L, "Python", "2P", date, date, null),
                new Course(3L, "C#", "3C", date, date, null)
        );

        List<CourseDto> courseDtoList = List.of(
                new CourseDto(1L, "Java", "1J", date, date, null),
                new CourseDto(2L, "Python", "2P", date, date, null),
                new CourseDto(3L, "C#", "3C", date, date, null)
        );


        when(courseRepository.findAll()).thenReturn(courseList);
        when(courseMapper.toDtoList(courseList)).thenReturn(courseDtoList);

        List<CourseDto> result = courseService.getAllCourses();

        assertEquals(courseDtoList.size(), result.size());
        assertEquals("Python", result.get(1).getCourseName());
        assertEquals(courseDtoList, result);

        verify(courseRepository, times(1)).findAll();
        verify(courseMapper, times(1)).toDtoList(courseList);

    }

    //Тест на Получение курса по ID
    @Test
    void getCourseById() {
        Long findId = 1L;

        when(courseRepository.findById(findId)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(courseDto);

        CourseDto result = courseService.getCourseById(findId);

        assertEquals(courseDto, result);
        assertEquals("Java", result.getCourseName());
        verify(courseRepository, times(1)).findById(findId);
    }

    //Тест на Получение курса по ID - не найден - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getCourseById_NotFoundException() {
        Long findId = 999L;

        when(courseRepository.findById(findId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> courseService.getCourseById(findId));

        assertEquals("Курс с id: 999 - не существует", exception.getMessage());
        verify(courseRepository, times(1)).findById(findId);
    }

    //Тест на Создание курса
    @Test
    void createCourse() {

        when(courseMapper.toEntity(courseDto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toDto(course)).thenReturn(courseDto);

        CourseDto result = courseService.createCourse(courseDto);

        assertEquals(courseDto, result);
        assertEquals("Java", result.getCourseName());
        verify(courseRepository, times(1)).save(course);

    }

    //Тест на создание курса, курс с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createExistsCourseName_handleDataIntegrityViolationException() {

        when(courseMapper.toEntity(courseDto)).thenReturn(course);
        when(courseRepository.save(course)).thenThrow(new DataIntegrityViolationException(course.getCourseName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> courseService.createCourse(courseDto));

        assertTrue(exception.getMessage().contains("Курс с таким названием уже существует: Java"));
        verify(courseRepository, times(1)).save(course);
    }

    //Тест на создание курса, отправлено пустое название курса - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createCourseWithEmptyName_IllegalArgumentException() {
        CourseDto emptyCourseDto = new CourseDto(1L, "", "1J", date, date, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(emptyCourseDto));
        assertEquals("Названия курса не может быть пустым", exception.getMessage());
    }

    //Тест на обновление курса
    @Test
    void updateCourse() {

        CourseDto newCourseDto = new CourseDto(1L, "J_Java Developer", "2J", date, date, null);
        Course newCourse = new Course(1L, "J_Java Developer", "2J", date, date, null);

        when(courseRepository.findById(newCourseDto.getId())).thenReturn(Optional.of(course));
        when(courseMapper.toEntity(newCourseDto)).thenReturn(newCourse);
        when(courseRepository.save(newCourse)).thenReturn(newCourse);
        when(courseMapper.toDto(newCourse)).thenReturn(newCourseDto);

        CourseDto result = courseService.updateCourse(newCourseDto);

        assertEquals(result, newCourseDto);
        assertEquals("J_Java Developer", result.getCourseName());
        assertEquals(newCourseDto.getCourseName(), result.getCourseName());
        assertEquals("2J", result.getCourseDescription());
        assertEquals(newCourseDto.getUpdatedTime(), result.getUpdatedTime());

        verify(courseRepository, times(1)).findById(newCourseDto.getId());
        verify(courseRepository, times(1)).save(newCourse);
        verify(courseMapper, times(1)).toEntity(newCourseDto);


    }

    //Тест на обновление курса - курс с таким Id не существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateCourseNotFoundById_NotFoundException() {
        Long findId = 999L;
        CourseDto fakeCourseDto = new CourseDto(findId, "Java", "1J", date, date, null);

        when(courseRepository.findById(findId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> courseService.updateCourse(fakeCourseDto));

        assertEquals("Курс с id: 999 - не существует", exception.getMessage());
        verify(courseRepository, times(1)).findById(findId);
    }

    //Тест на обновление курса - отправлено пустое название курса - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateCourseWithEmptyName_IllegalArgumentException() {
        CourseDto courseDto = new CourseDto(1L, "", "1J", date, date, null);

        when(courseRepository.findById(courseDto.getId())).thenReturn(Optional.of(course));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(courseDto));

        assertEquals("Названия курса не может быть пустым", exception.getMessage());
    }

    //Тест на обновление курса - курса с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateCourseWithExistsName_handleDataIntegrityViolationException() {

        CourseDto newCourseDto = new CourseDto(1L, "Java Mobile", "1J", date, date, null);
        Course newCourse = new Course(1L, "Java Mobile", "1J", date, date, null);

        when(courseRepository.findById(courseDto.getId())).thenReturn(Optional.of(course));
        when(courseMapper.toEntity(newCourseDto)).thenReturn(newCourse);
        when(courseRepository.save(newCourse)).
                thenThrow(new DataIntegrityViolationException(newCourse.getCourseName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> courseService.updateCourse(newCourseDto));

        assertEquals("Курс с таким названием уже существует: Java Mobile", exception.getMessage());

        verify(courseRepository, times(1)).findById(courseDto.getId());
        verify(courseRepository, times(1)).save(newCourse);
        verify(courseMapper, times(1)).toEntity(newCourseDto);
    }

    //Тест на удаление курса
    @Test
    void deleteCourse() {
        Long findId = 1L;
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        courseService.deleteCourseById(findId);

        verify(courseRepository, times(1)).findById(findId);
    }

    //Тест на удаление курса - курса не найден - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void deleteCourseNotFoundById_NotFoundException() {
        Long finId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> courseService.deleteCourseById(finId));

        assertEquals("Курс с id: 999 - не существует", exception.getMessage());
    }
}
