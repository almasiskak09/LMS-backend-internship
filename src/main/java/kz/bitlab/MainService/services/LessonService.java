package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.LessonDto;
import java.util.List;
public interface LessonService {

    //Получение списка всех уроков
     List<LessonDto> getAllLessons();

    //Поиск списка уроков по ID главы, Уроки которые относятся к конкретной главе
     List<LessonDto> getAllLessonsByChapterId(Long chapterId);

    //Получение урока по ID
     LessonDto getLessonById(Long id);

    //Создание урока
     LessonDto createLesson(LessonDto lessonDto);

    //Обновление урока
     LessonDto updateLesson(LessonDto lessonDto);

    //Удаление урока по ID
     void deleteLessonById(Long id);

}
