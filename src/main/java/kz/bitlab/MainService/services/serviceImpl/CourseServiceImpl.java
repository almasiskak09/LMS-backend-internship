package kz.bitlab.MainService.services.serviceImpl;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.CourseMapper;
import kz.bitlab.MainService.repository.CourseRepository;
import kz.bitlab.MainService.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    //Получение списка всех курсов
    public List<CourseDto> getAllCourses () {
        List<CourseDto> courseDtoList = courseMapper.toDtoList(courseRepository.findAll());
        log.info("Был выполнен поиск списка всех курсов");
        return courseDtoList;
    }
    //Получение курса по ID
    public CourseDto getCourseById(Long id){
        Course foundCourse = foundCourseById(id);
        log.info("Курс по id:{} - был найден", id);
        return courseMapper.toDto(foundCourse);
    }

    //Создание курса
    public CourseDto createCourse(CourseDto courseDto){
        validCourseName(courseDto.getCourseName());
        Course addingCourse = courseMapper.toEntity(courseDto);
        try {
            courseRepository.save(addingCourse);
            log.info("Курс с названием: {} - был добавлен", addingCourse.getCourseName());
            return courseMapper.toDto(addingCourse);
        }
        catch (DataIntegrityViolationException e){ //ошибка на уникальность
            handleDataIntegrityViolationException(e,courseDto.getCourseName());
        }
        return null;
    }

    //Обновление курса
    public CourseDto updateCourse(CourseDto courseDto){
        Course findCourse = foundCourseById(courseDto.getId());
        validCourseName(courseDto.getCourseName());
        try {
            findCourse.setCourseName(courseDto.getCourseName());
            findCourse.setCourseDescription(courseDto.getCourseDescription());
            findCourse.setUpdatedTime(LocalDateTime.now());

            Course savedCourse = courseRepository.save(findCourse);
            log.info("Курс по названию: {} - был обновлен на: {}", findCourse.getCourseName(), courseDto.getCourseName());
            return courseMapper.toDto(savedCourse);
        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e,courseDto.getCourseName());
        }
        return null;
    }

    //Удаление курса по ID
    public void deleteCourseById(Long id){
        getCourseById(id);
        courseRepository.deleteById(id);
        log.info("Курс с id: {} - удален", id);
    }

    // другие методы
    // Поиск главы по ID. Проверка существует ли такой курс
    private Course foundCourseById(Long id) {
        return courseRepository.findById(id).
                orElseThrow(()->{
                    log.error("Курс с id: {} - не существует", id);
                    return new NotFoundException("Курс с id: "+id+" - не существует");
                });
    }

    // Проверка название главы на пустое значение
    private void validCourseName(String chapterName){
        if(chapterName== null || chapterName.isEmpty()){
            log.error("Названия курса не может быть пустым");
            throw new IllegalArgumentException("Названия курса не может быть пустым");
        }

    }

    //Проверка на уникальность имени курса. Существует ли курс с таким же именем.
    public void handleDataIntegrityViolationException(DataIntegrityViolationException e,String courseName) {
        String message = e.getMessage();
        log.error("Курс с таким названием уже существует: {}", message);
        throw new DataIntegrityViolationException("Курс с таким названием уже существует: "+ courseName);
    }

}
