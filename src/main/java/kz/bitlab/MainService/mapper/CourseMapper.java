package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toEntity(CourseDto courseDto);
    CourseDto toDto(Course course);
    List<Course> toEntityList(List<CourseDto> courseDtoList);
    List<CourseDto> toDtoList(List<Course> courseList);
}
