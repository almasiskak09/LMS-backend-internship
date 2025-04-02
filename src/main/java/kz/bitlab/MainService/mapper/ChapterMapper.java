package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(source = "courseId",target = "course", qualifiedByName = "mapCourseFromId")
    Chapter toChapterEntity(ChapterDto chapterDto);

    @Mapping(source = "course.id", target = "courseId")
    ChapterDto toChapterDto(Chapter chapter);

    List<Chapter> toChapterEntityList(List<ChapterDto> chapterDtoList);
    List<ChapterDto> toChapterDtoList(List<Chapter> chapterList);

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
