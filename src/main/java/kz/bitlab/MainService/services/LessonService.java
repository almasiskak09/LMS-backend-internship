package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.LessonMapper;
import kz.bitlab.MainService.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    //Получение списка всех уроков
    public List<LessonDto> getAllLessons(){
        List<Lesson> lessonList = lessonRepository.findAll();
        log.info("Был выполнен поиск список всех уроков");

        return lessonMapper.toDtoList(lessonList);
    }

    //Поиск списка уроков по ID главы, Уроки которые относятся к конкретной главе
    public List<LessonDto> getAllLessonsByChapterId(Long chapterId){
        validChapterId(chapterId);

        List<Lesson> lessonList = lessonRepository.findAllLessonsByChapterId(chapterId);
        if(lessonList.isEmpty()){
            log.warn("Нет уроков по данному id главы: {}",chapterId);
            throw new NotFoundException ("Нет уроков по данному id главы: "+ chapterId);
        } else{
            log.info("Найдено {} уроков для главы с id: {}", lessonList.size(), chapterId);
        }
        return lessonMapper.toDtoList(lessonList);
    }

    //Получение урока по ID
    public LessonDto getLessonById(Long id){

        Lesson lesson = foundLessonById(id);
        log.info("Был выполнен поиск урока по ID: {}",id);
        return lessonMapper.toDto(lesson);
    }

    //Создание урока
    public LessonDto createLesson(LessonDto lessonDto){

        validChapterId(lessonDto.getChapterId());

        validLessonName(lessonDto.getLessonName());

        Lesson lesson = lessonMapper.toEntity(lessonDto);
        try {
            lessonRepository.save(lesson);
            log.info("Урок с названием: {} - был добавлен", lessonDto.getLessonName());
            return lessonMapper.toDto(lesson);
        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e,lessonDto.getLessonName());
        }
        return null;
    }

    //Обновление урока
    public LessonDto updateLesson(LessonDto lessonDto){
        Lesson foundLesson = foundLessonById(lessonDto.getId());
        validLessonName(lessonDto.getLessonName());

        try {
            foundLesson.setLessonName(lessonDto.getLessonName());
            foundLesson.setLessonContent(lessonDto.getLessonContent());
            foundLesson.setLessonDescription(lessonDto.getLessonDescription());
            foundLesson.setUpdatedTime(LocalDateTime.now());

            Lesson savedLesson = lessonRepository.save(foundLesson);

            log.info("Урок по названию: {} - была обновлена", savedLesson.getLessonName());
            return lessonMapper.toDto(savedLesson);

        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e,lessonDto.getLessonName());
        }
        return null;
    }

    //Удаление урока по ID
    public void deleteLessonById(Long id){
        foundLessonById(id);
        lessonRepository.deleteById(id);
        log.info("Урок с id:{} - была удалена",id);

    }

    // другие методы
    // Поиск урока по ID. Проверка существует ли такой урок
    private Lesson foundLessonById(Long id){
    return lessonRepository.findById(id).
            orElseThrow(() -> {
                log.error("Урок по id: {} - не существует", id);
                return new NotFoundException("Урок по id: "+id+" - не существует");
            });
    }

    // Проверка отправленного ID на пустое или неправильное значение (меньше нуля)
    private void validChapterId(Long chapterId){
        if(chapterId == null || chapterId <=0){
            log.error("Пожалуйста, укажите корректный ID главы.");
            throw new IllegalArgumentException("Пожалуйста, укажите корректный ID главы.");
        }
    }

    // Проверка название урока на пустое значение
    private void validLessonName(String lessonName){
        if(lessonName == null || lessonName.isEmpty()){
            log.error("Название урока не может быть пустым");
            throw new IllegalArgumentException("Название урока не может быть пустым");
        }

    }

    //Проверка на уникальность имени урока. Существует ли урок с таким же именем.
    private void handleDataIntegrityViolationException(DataIntegrityViolationException e,String lessnName){
        String message = e.getMessage();
        log.error("Урок с таким названием уже существует: {} ", lessnName);
        throw new DataIntegrityViolationException("Урок с таким названием уже существует: " + lessnName );
    }

}
