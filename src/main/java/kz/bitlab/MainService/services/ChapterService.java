package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.ChapterMapper;
import kz.bitlab.MainService.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    //Получение списка всех глав
    public List<ChapterDto> getAllChapters(){
        List<Chapter> chapterList = chapterRepository.findAll();
        log.info("Был выполнен поиск списка всех глав курса");

        return chapterMapper.toDtoList(chapterList);
    }

    //Получение списка глав по ID курса, список глав которые относятся к конкретному курсу
    public List<ChapterDto> getAllChaptersByCourseId(Long courseId){
        validCourseId(courseId);

        List<Chapter> chapterList = chapterRepository.findChaptersByCourseId(courseId);
        if(chapterList.isEmpty()){
            log.info("Нет глав по id курса: {} ", courseId);
            throw new NotFoundException("Нет глав по id курса: "+courseId);
        }else{
            log.info("Найдено {} глав по курс с id: {} ", chapterList.size(), courseId);
        }
        return chapterMapper.toDtoList(chapterList);
    }

    //Получение главы по ID
    public ChapterDto getChapterById(Long id){
        Chapter chapter = foundChapterById(id);
        log.info("Был выполнен поиск главы по ID: {}",id);
        return chapterMapper.toDto(chapter);
    }

    //Создание главы
    public ChapterDto createChapter(ChapterDto chapterDto) {

        validCourseId(chapterDto.getCourseId());
        validChapterName(chapterDto.getChapterName());

        Chapter addingChapter = chapterMapper.toEntity(chapterDto);
        try {
            chapterRepository.save(addingChapter);
            log.info("Глава с названием: {} - был добавлен", addingChapter.getChapterName());
            return chapterMapper.toDto(addingChapter);
        }catch (DataIntegrityViolationException e){
            handleDataIntegrityViolationException(e,chapterDto.getChapterName());
        }
        return null;
    }

    //Обновление главы
    public ChapterDto updateChapter(ChapterDto chapterDto) {
        foundChapterById(chapterDto.getId());
        validChapterName(chapterDto.getChapterName());

        try {
            Chapter savingChapter = chapterRepository.save(chapterMapper.toEntity(chapterDto));
            log.info("Глава по названию: {} - была обновлена", savingChapter.getChapterName());
            return chapterMapper.toDto(savingChapter);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolationException(e,chapterDto.getChapterName());
        }
        return null;
    }

    //Удаление главы по ID
    public void deleteChapterById(Long id) {
        foundChapterById(id);
        chapterRepository.deleteById(id);
        log.info("Глава с id:{} - была удалена",id);
    }

    // другие методы
    // Поиск главы по ID. Проверка существует ли такая глава
    private Chapter foundChapterById(Long id) {
        return chapterRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Глава курса по id: {} - не существует ", id);
                    return new NotFoundException("Глава курса по id: "+id+" - не существует");
                });
    }

    // Проверка название главы на пустое значение
    private void validChapterName(String chapterName) {
        if(chapterName== null || chapterName.isEmpty()){
            log.error("Названия главы не может быть пустым");
            throw new IllegalArgumentException("Названия главы не может быть пустым");
        }

    }

    // Проверка отправленного ID на пустое или неправильное значение (меньше нуля)
    private void validCourseId(Long courseId) {
        if(courseId == null || courseId <= 0){
            log.error("Пожалуйста, укажите корректный ID курса.");
            throw new IllegalArgumentException("Пожалуйста, укажите корректный ID курса.");
        }
    }

    //Проверка на уникальность имени главы. Существует ли глава с таким же именем.
    private void handleDataIntegrityViolationException(DataIntegrityViolationException e,String chapterName) {
        String message = e.getMessage();
        log.error("Глава с таким названием уже существует: {}", message);
        throw new DataIntegrityViolationException("Глава с таким названием уже существует: "+chapterName );
    }






}