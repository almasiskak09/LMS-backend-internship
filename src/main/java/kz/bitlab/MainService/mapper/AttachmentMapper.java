package kz.bitlab.MainService.mapper;

import kz.bitlab.MainService.dto.AttachmentDto;
import kz.bitlab.MainService.entity.Attachment;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(source = "lessonId", target = "lesson", qualifiedByName = "mapLessonFromId")
    Attachment toEntity(AttachmentDto attachmentDto);

    @Mapping(source = "lesson.id", target = "lessonId")
    AttachmentDto toDto(Attachment attachment);

    List<AttachmentDto> toDto(List<Attachment> attachmentList);
    List<Attachment> toEntity(List<AttachmentDto> attachmentDtoList);

    @Named("mapLessonFromId")
    default Lesson mapLessonFromId(Long lessonId) {
        if(lessonId == null){
            return null;
        }
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        return lesson;
    }
}
