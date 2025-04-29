package kz.bitlab.MainService.services.minio;


import kz.bitlab.MainService.dto.AttachmentDto;
import kz.bitlab.MainService.entity.Attachment;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.mapper.AttachmentMapper;
import kz.bitlab.MainService.repository.AttachmentRepository;
import kz.bitlab.MainService.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MinioFileService minioFileService;
    private final LessonRepository lessonRepository;
    private final AttachmentMapper attachmentMapper;

    public AttachmentDto uploadFile(MultipartFile file, Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));


        Attachment attachment = new Attachment();
        attachment.setName(file.getOriginalFilename());
        attachment.setUrl(file.getOriginalFilename()); // имя файла как url для скачивания
        attachment.setLesson(lesson);
        attachment.setCreatedTime(LocalDateTime.now());

        Attachment saved = attachmentRepository.save(attachment);

        return attachmentMapper.toDto(saved);
    }

    public ByteArrayResource downloadFile(String fileName) {
        return minioFileService.downloadFile(fileName);
    }
}
