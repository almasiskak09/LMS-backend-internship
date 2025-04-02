package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(source = "chapterId", target = "chapter", qualifiedByName = "mapChapterFromId")
    Lesson toLessonEntity(LessonDto lessonDto);

    @Mapping(source = "chapter.id", target = "chapterId")
    LessonDto toLessonDto(Lesson lesson);

    List<Lesson>toLessonEntityList(List<LessonDto> lessonDtoList);
    List<LessonDto> toLessonDtoList(List<Lesson> lessonList);

    @Named("mapChapterFromId")
    default Chapter mapChapterFromId(Long chapterId) {
        if (chapterId == null) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        return chapter;
    }
}
