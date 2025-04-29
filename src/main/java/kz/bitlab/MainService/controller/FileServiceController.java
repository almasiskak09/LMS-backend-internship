package kz.bitlab.MainService.controller;

import com.google.common.net.HttpHeaders;
import kz.bitlab.MainService.dto.AttachmentDto;
import kz.bitlab.MainService.services.minio.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/file")
public class FileServiceController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<AttachmentDto> upload(@RequestParam("file") MultipartFile file,
                                                @RequestParam("lessonId") Long lessonId) {
        AttachmentDto dto = attachmentService.uploadFile(file, lessonId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping(value = "/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable(name = "fileName") String fileName) {
        ByteArrayResource fileResource = attachmentService.downloadFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Это универсальный MIME-тип для файлов
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileResource);
    }
}
