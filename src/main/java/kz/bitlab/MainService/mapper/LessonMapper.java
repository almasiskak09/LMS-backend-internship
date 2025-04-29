package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = AttachmentMapper.class)
public interface LessonMapper {

    @Mapping(source = "chapterId", target = "chapter", qualifiedByName = "mapChapterFromId")
    @Mapping(source = "attachmentDtoList", target = "attachments")
    Lesson toEntity(LessonDto lessonDto);

    @Mapping(source = "chapter.id", target = "chapterId")
    @Mapping(source = "attachments", target = "attachmentDtoList")
    LessonDto toDto(Lesson lesson);

    List<Lesson>toEntityList(List<LessonDto> lessonDtoList);
    List<LessonDto> toDtoList(List<Lesson> lessonList);

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
