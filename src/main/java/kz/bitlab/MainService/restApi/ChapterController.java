package kz.bitlab.MainService.restApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.services.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/chapter")
@RequiredArgsConstructor
@Tag(name = "Главы курсов", description = "Управление списком глав каждого курса")
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping
    @Operation(summary = "Получить список всех глав")
    public ResponseEntity<List<ChapterDto>> getAllChapters() {
        return ResponseEntity.ok(chapterService.getAllChapters());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получение главы по ID",
                description = "Получение главы по определенному ID, вместе с вложенными уроками")
    public ResponseEntity<ChapterDto> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterById(id));
    }

    @GetMapping(value = "/course/{id}")
    @Operation(summary = "Получение списка глав по ID курса",
             description = "Позволяет получить список глав, относящихся к конкретному курсу, идентифицированному по его уникальному ID")
    public ResponseEntity<List<ChapterDto>> getChaptersByCourseId(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getAllChaptersByCourseId(id));
    }

    @PostMapping
    @Operation(summary = "Добавление главы",
            description = "Добавление главы с указанием ID курса.К какому курсу принадлежит данная глава"
                )
    public ResponseEntity<ChapterDto> createChapter(@RequestBody ChapterDto chapterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapterService.createChapter(chapterDto));
    }

    @PutMapping
    @Operation(summary = "Обновление главы")
    public ResponseEntity<ChapterDto> updateChapter(@RequestBody ChapterDto chapterDto) {
        return ResponseEntity.ok(chapterService.updateChapter(chapterDto));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удаление главы по ID")
    public ResponseEntity<Void> deleteChapterById(@PathVariable Long id) {
        chapterService.deleteChapterById(id);
        return ResponseEntity.noContent().build();
    }

}
