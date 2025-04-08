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

    public LessonDto getLessonById(Long id){

        Lesson lesson = foundLessonById(id);
        log.info("Был выполнен поиск урока по ID: {}",id);
        return lessonMapper.toDto(lesson);
    }

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

    public LessonDto updateLesson(LessonDto lessonDto){
        foundLessonById(lessonDto.getId());
        validLessonName(lessonDto.getLessonName());

        try {
            Lesson savingLesson = lessonRepository.save(lessonMapper.toEntity(lessonDto));
            log.info("Урок по названию: {} - была обновлена", savingLesson.getLessonName());
            return lessonMapper.toDto(savingLesson);

        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e,lessonDto.getLessonName());
        }
        return null;
    }

    public void deleteLessonById(Long id){
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

    private void validChapterId(Long chapterId){
        if(chapterId == null || chapterId <=0){
            log.error("Пожалуйста, укажите корректный ID главы.");
            throw new IllegalArgumentException("Пожалуйста, укажите корректный ID главы.");
        }
    }

    private void validLessonName(String lessonName){
        if(lessonName == null || lessonName.isEmpty()){
            log.error("Название урока не может быть пустым");
            throw new IllegalArgumentException("Название урока не может быть пустым");
        }

    }

    private void handleDataIntegrityViolationException(DataIntegrityViolationException e,String lessnName){
        String message = e.getMessage();
        log.error("Урок с таким названием уже существует: {} ", lessnName);
        throw new DataIntegrityViolationException("Урок с таким названием уже существует: " + lessnName );
    }

}
