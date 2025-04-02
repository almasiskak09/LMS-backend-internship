package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.mapper.CourseMapper;
import kz.bitlab.MainService.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Course foundCourse = courseRepository.findById(id).
                                    orElseThrow(()->{
                                    log.error("Курс с id: {} - не найден", id);
                                    return new RuntimeException("Курс по данному id: "+id+" - не найден");
                                    });
        log.info("Курс по id:{} - был найден", id);
        return courseMapper.toDto(foundCourse);
    }

    public CourseDto createCourse(CourseDto courseDto){
        if(courseDto == null){
            throw new IllegalArgumentException("Данные курса не могут быть null");
        }
        Course addingCourse = courseMapper.toEntity(courseDto);
        courseRepository.save(addingCourse);

        log.info("Курс с названием: {} - был добавлен", addingCourse.getCourseName());

        return courseMapper.toDto(addingCourse);

    }

    public CourseDto updateCourse(CourseDto courseDto){
        Course foundCourse = courseRepository.findById(courseDto.getId()).
                                    orElseThrow(()-> new RuntimeException("Курс по данному id: "+courseDto.getCreatedTime()+" - не найден"));
        Course savedCourse = courseRepository.save(courseMapper.toEntity(courseDto));

        log.info("Курс с id: {} - был добавлен", courseDto.getId());

        return courseMapper.toDto(savedCourse);
    }

    public void deleteCourseById(Long id){
        courseRepository.deleteById(id);
        log.info("Курс с id: {} - удален", id);
    }
}
