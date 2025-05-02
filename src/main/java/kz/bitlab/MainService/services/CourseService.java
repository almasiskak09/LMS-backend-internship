package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.CourseDto;
import java.util.List;


public interface CourseService {


    //Получение списка всех курсов
     List<CourseDto> getAllCourses ();

    //Получение курса по ID
     CourseDto getCourseById(Long id);

    //Создание курса
     CourseDto createCourse(CourseDto courseDto);

    //Обновление курса
     CourseDto updateCourse(CourseDto courseDto);

    //Удаление курса по ID
     void deleteCourseById(Long id);


}
