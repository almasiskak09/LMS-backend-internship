package kz.bitlab.MainService.restController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.services.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/lesson")
@RequiredArgsConstructor
@Tag(name = "Уроки",description = "Управление списком уроков")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    @Operation(summary = "Получить список всех уроков")
    public ResponseEntity<List<LessonDto>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получение конкретного урока по ID")
    public ResponseEntity<LessonDto> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @GetMapping(value = "/chapter/{id}")
    @Operation(summary = "Получение списка уроков по ID главы",
            description = "Позволяет получить список уроков, относящихся к конкретной главе, идентифицированному по его уникальному ID")
    public ResponseEntity<List<LessonDto>> getLessonsByChapterId(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getAllLessonsByChapterId(id));
    }

    @PostMapping
    @Operation(summary = "Добавление урока")
    public ResponseEntity<LessonDto> createLesson(@RequestBody LessonDto lessonDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(lessonDto));
    }

    @PutMapping
    @Operation(summary = "Обновление конкретного урока")
    public ResponseEntity<LessonDto> updateLesson(@RequestBody LessonDto lessonDto) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonDto));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удаление урока по ID")
    public ResponseEntity<Void> deleteLessonById(@PathVariable Long id) {
        lessonService.deleteLessonById(id);
        return ResponseEntity.noContent().build();
    }
}
