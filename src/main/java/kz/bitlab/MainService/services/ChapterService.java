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

    public List<ChapterDto> getAllChapters(){
        List<Chapter> chapterList = chapterRepository.findAll();
        log.info("Был выполнен поиск списка всех глав курса");

        return chapterMapper.toDtoList(chapterList);
    }

    public List<ChapterDto> getAllChaptersByCourseId(Long courseId){
        if(courseId == null || courseId <= 0){
            log.error("ID курса не может быть null");
            throw new IllegalArgumentException("ID курса не может быть null");
        }

        List<Chapter> chapterList = chapterRepository.findChaptersByCourseId(courseId);
        if(chapterList.isEmpty()){
            log.warn("Нет глав по id урока: {} ", courseId);
        }else{
            log.info("Найдено {} глав по курс с id: {} ", chapterList.size(), courseId);
        }
        return chapterMapper.toDtoList(chapterList);
    }

    public ChapterDto getChapterById(Long id){
        Chapter chapter = foundChapterById(id);
        log.info("Был выполнен поиск главы по ID: {}",id);
        return chapterMapper.toDto(chapter);
    }

    public ChapterDto createChapter(ChapterDto chapterDto) {
        validChapterName(chapterDto.getChapterName());

        Chapter addingChapter = chapterMapper.toEntity(chapterDto);
        try {
            chapterRepository.save(addingChapter);
            log.info("Глава с названием: {} - был добавлен", addingChapter.getChapterName());
            return chapterMapper.toDto(addingChapter);
        }catch (DataIntegrityViolationException e){
            DataIntegrityViolationException(e);
        }
        return null;
    }


    public ChapterDto updateChapter(ChapterDto chapterDto) {
        foundChapterById(chapterDto.getId());
        validChapterName(chapterDto.getChapterName());


        try {
            Chapter savingChapter = chapterRepository.save(chapterMapper.toEntity(chapterDto));
            log.info("Глава по названию: {} - была обновлена", savingChapter.getChapterName());
            return chapterMapper.toDto(savingChapter);
        } catch (DataIntegrityViolationException e) {
            DataIntegrityViolationException(e);
        }
        return null;
    }


    public void deleteChapter(Long id) {
        foundChapterById(id);
        chapterRepository.deleteById(id);
        log.info("Глава с id:{} - была удалена",id);
    }


    // другие методы

    private Chapter foundChapterById(Long id) {
        return chapterRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Глава курса по id: {} - не существует ", id);
                    return new NotFoundException("Глава курса по id: "+id+" - не существует");
                });
    }

    private void validChapterName(String chapterName) {
        if(chapterName== null || chapterName.isEmpty()){
            log.error("Названия главы не может быть пустым");
            throw new RuntimeException("Названия главы не может быть пустым");
        }

    }

    private void DataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        log.error("Глава с такими данными уже существует: {}", message);
        throw new RuntimeException("Глава с такими данными уже существует: "+ message );
    }






}