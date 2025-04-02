package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.mapper.LessonMapper;
import kz.bitlab.MainService.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;


    public List<LessonDto> getAllLessons(){
        List<Lesson> lessonList = lessonRepository.findAll();
        log.info("Был выполнен поиск список всех уроков");

        return lessonMapper.toDtoList(lessonList);
    }

    public List<LessonDto> getAllLessonsByChapter(Long id){
        List<Lesson> lessonList = lessonRepository.findAllLessonsByChapterId(id);
        if(lessonList.isEmpty()){
            log.warn("Нет уроков по данному id главы: {}",id);
        } else{
            log.info("Найдено {} уроков для главы с id: {}", lessonList.size(), id);
        }
        return lessonMapper.toDtoList(lessonList);
    }


    public LessonDto getLessonById(Long id){
        Lesson lesson = lessonRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Урок по id: {} - не был найден ", id);
                    return new RuntimeException("Урок по id: "+id+" - не был найден");
                });
        return lessonMapper.toDto(lesson);
    }

    public LessonDto createLesson(LessonDto lessonDto){
        Lesson lesson = lessonMapper.toEntity(lessonDto);
        Lesson savedLesson = lessonRepository.save(lesson);

        log.info("Урок с названием: {} - был добавлен", lessonDto.getLessonName());

        return lessonMapper.toDto(savedLesson);
    }

    public LessonDto updateLesson(LessonDto lessonDto){
        Lesson foundLesson = lessonRepository.findById(lessonDto.getId()).
                orElseThrow(() -> {
                    log.error("Урок по id: {} - не был найден", lessonDto.getId());
                    return new RuntimeException("Урок по id: " + lessonDto.getId() + " - не был найден");
                });
        Lesson savingLesson = lessonRepository.save(lessonMapper.toEntity(lessonDto));
        log.info("Глава по названию: {} - была обновлена", savingLesson.getLessonName());

        return lessonMapper.toDto(savingLesson);
    }

    public void deleteLesson(Long id){
        lessonRepository.deleteById(id);
        log.info("Урок с id:{} - был удален",id);

    }

}
