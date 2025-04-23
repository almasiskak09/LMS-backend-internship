package kz.bitlab.MainService.restController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/course")
@Tag(name = "Курсы", description = "Управление списком курсов")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Получить список всех курсов",
            description = "Возвращает список всех имеющихся курсов вместе с вложенными главами (Chapter)")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получить определенный курс по ID",
            description = "Возвращает определенный курс по требуемой ID, вместе с вложенными главами (Chapter)")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id){
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавление курса")
    public ResponseEntity<CourseDto> addCourse( @RequestBody CourseDto courseDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(courseDto));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновление курса",
                description = "Обновляет курс по отправленному DTO объекту")
    public ResponseEntity<CourseDto> updateCourse(@RequestBody CourseDto courseDto){
        return ResponseEntity.ok(courseService.updateCourse(courseDto));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удаление курса по его ID")
    public ResponseEntity<Void> deleteCourseById(@PathVariable Long id){
        courseService.deleteCourseById(id);
        return ResponseEntity.noContent().build();
    }
}
