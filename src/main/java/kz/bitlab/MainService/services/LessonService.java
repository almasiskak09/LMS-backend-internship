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

    public List<LessonDto> getAllLessonsByChapter(Long chapterId){
        if(chapterId == null || chapterId <=0){
            log.error("ID главы не может быть null");
            throw new IllegalArgumentException("ID главы не может быть null");
        }
        List<Lesson> lessonList = lessonRepository.findAllLessonsByChapterId(chapterId);
        if(lessonList.isEmpty()){
            log.warn("Нет уроков по данному id главы: {}",chapterId);
        } else{
            log.info("Найдено {} уроков для главы с id: {}", lessonList.size(), chapterId);
        }
        return lessonMapper.toDtoList(lessonList);
    }

    public LessonDto getLessonById(Long id){

        Lesson lesson = foundLessonById(id);
        log.info("Был выполнен поиск урока по ID: {}",id);
        return lessonMapper.toDto(lesson);
    }

    public LessonDto createLesson(LessonDto lessonDto){
        validLessonName(lessonDto.getLessonName());

        Lesson lesson = lessonMapper.toEntity(lessonDto);
        try {
            lessonRepository.save(lesson);
            log.info("Урок с названием: {} - был добавлен", lessonDto.getLessonName());
            return lessonMapper.toDto(lesson);
        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e);
        }
        return null;
    }

    public LessonDto updateLesson(LessonDto lessonDto){
        validLessonName(lessonDto.getLessonName());
        foundLessonById(lessonDto.getId());

        try {
            Lesson savingLesson = lessonRepository.save(lessonMapper.toEntity(lessonDto));
            log.info("Урок по названию: {} - была обновлена", savingLesson.getLessonName());
            return lessonMapper.toDto(savingLesson);

        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e);
        }
        return null;
    }

    public void deleteLesson(Long id){
        foundLessonById(id);
        lessonRepository.deleteById(id);
        log.info("Урок с id:{} - была удалена",id);

    }

    // другие методы

    private Lesson foundLessonById(Long id){
    return lessonRepository.findById(id).
            orElseThrow(() -> {
                log.error("Урок по id: {} - не существует", id);
                return new NotFoundException("Урок по id: "+id+" - не существует");
            });
    }

    private void validLessonName(String lessonName){
        if(lessonName == null || lessonName.isEmpty()){
            log.error("Название урока не может быть null");
            throw new IllegalArgumentException("Название урока не может быть null");
        }

    }

    private void handleDataIntegrityViolationException(DataIntegrityViolationException e){
        String message = e.getMessage();
        log.error("Урок с таким названием уже существует: {} " + message);
        throw new RuntimeException("Урок с таким названием уже существует: "+ message );
    }

}
