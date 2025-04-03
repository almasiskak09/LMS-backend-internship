package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = LessonMapper.class)
public interface ChapterMapper {

    @Mapping(source = "courseId",target = "course", qualifiedByName = "mapCourseFromId")
    @Mapping(source = "lessonDtoList", target = "lessonList")
    Chapter toEntity(ChapterDto chapterDto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "lessonList", target = "lessonDtoList")
    ChapterDto toDto(Chapter chapter);

    List<Chapter> toEntityList(List<ChapterDto> chapterDtoList);
    List<ChapterDto> toDtoList(List<Chapter> chapterList);

    @Named("mapCourseFromId")
    default Course mapCourseFromId(Long courseId) {
        if(courseId == null){
            return null;
        }
        Course course = new Course();
        course.setId(courseId);
        return course;
    }

}
