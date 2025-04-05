package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.CourseMapper;
import kz.bitlab.MainService.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public List<CourseDto> getAllCourses () {
        List<CourseDto> courseDtoList = courseMapper.toDtoList(courseRepository.findAll());
        log.info("Был выполнен поиск списка всех курсов");
        return courseDtoList;
    }

    public CourseDto findCourseById(Long id){
        Course foundCourse = foundCourseById(id);
        log.info("Курс по id:{} - был найден", id);
        return courseMapper.toDto(foundCourse);
    }

    public CourseDto createCourse(CourseDto courseDto){
        validCourseName(courseDto.getCourseName());
        Course addingCourse = courseMapper.toEntity(courseDto);
        try {
            courseRepository.save(addingCourse);
            log.info("Курс с названием: {} - был добавлен", addingCourse.getCourseName());
            return courseMapper.toDto(addingCourse);

        }
        catch (DataIntegrityViolationException e){ //ошибка на уникальность
            dataIntegrityViolationException(e);
        }
        return null;
    }

    public CourseDto updateCourse(CourseDto courseDto){
        foundCourseById(courseDto.getId());
        validCourseName(courseDto.getCourseName());
        try {
            Course savedCourse = courseRepository.save(courseMapper.toEntity(courseDto));
            log.info("Курс по названию: {} - был обновлен", savedCourse.getCourseName());
            return courseMapper.toDto(savedCourse);
        }catch (DataIntegrityViolationException e){
            dataIntegrityViolationException(e);
        }
        return null;
    }

    public void deleteCourseById(Long id){
       findCourseById(id);
        courseRepository.deleteById(id);
        log.info("Курс с id: {} - удален", id);
    }

    // другие методы

    private Course foundCourseById(Long id) {
        return courseRepository.findById(id).
                orElseThrow(()->{
                    log.error("Курс с id: {} - не существует", id);
                    return new NotFoundException("Курс с id: "+id+" - не существует");
                });
    }

    private void validCourseName(String chapterName) {
        if(chapterName== null || chapterName.isEmpty()){
            log.error("Названия курса не может быть пустым");
            throw new RuntimeException("Названия курса не может быть пустым");
        }

    }

    private void dataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        log.error("Кус с такими данными уже существует: {}", message);
        throw new RuntimeException("Кус с такими данными уже существует: "+ message );
    }

}
