package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toCourse(CourseDto courseDto);
    CourseDto toCourseDto(Course course);
    List<Course> toCourseEntityList(List<CourseDto> courseDtoList);
    List<CourseDto> toCourseDtoList(List<Course> courseList);
}
