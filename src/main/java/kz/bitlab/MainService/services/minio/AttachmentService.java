package kz.bitlab.MainService.services.minio;


import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kz.bitlab.MainService.dto.AttachmentDto;
import kz.bitlab.MainService.entity.Attachment;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.mapper.AttachmentMapper;
import kz.bitlab.MainService.repository.AttachmentRepository;
import kz.bitlab.MainService.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final LessonRepository lessonRepository;
    private final AttachmentMapper attachmentMapper;
    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket}")
    private String bucket;


    public AttachmentDto uploadFile(MultipartFile file, Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        String fileName = file.getOriginalFilename();

        String fileUrl = minioUrl + "/" + bucket + "/" + fileName;

        Attachment attachment = new Attachment();
        attachment.setName(fileName);
        attachment.setUrl(fileUrl);  // сохраняем полный URL
        attachment.setLesson(lesson);
        attachment.setCreatedTime(LocalDateTime.now());

        Attachment saved = attachmentRepository.save(attachment);

        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }

        return attachmentMapper.toDto(saved);
    }

    public ByteArrayResource downloadFile(String fileName){
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();

            InputStream stream = minioClient.getObject(getObjectArgs);
            byte [] byteArray = IOUtils.toByteArray(stream);
            stream.close();

            return new ByteArrayResource(byteArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
