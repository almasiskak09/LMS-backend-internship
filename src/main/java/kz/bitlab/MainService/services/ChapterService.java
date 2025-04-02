package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.mapper.ChapterMapper;
import kz.bitlab.MainService.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        List<Chapter> chapterList = chapterRepository.findChaptersByCourseId(courseId);
        if(chapterList.isEmpty()){
            log.warn("Нет глав по id урока: {} ", courseId);
        }else{
            log.info("Найдено {} глав по курс с id: {} ", chapterList.size(), courseId);
        }
        return chapterMapper.toDtoList(chapterList);
    }

    public ChapterDto getChapterById(Long id){
        Chapter chapter = chapterRepository.findById(id).
                           orElseThrow(() -> {
                               log.error("Глава курса по id: {} - не была найдена ", id);
                               return new RuntimeException("Глава курса по id: "+id+" - не была найдена");
                           });
        return chapterMapper.toDto(chapter);
    }

    public ChapterDto createChapter(ChapterDto chapterDto) {
        Chapter chapter = chapterMapper.toEntity(chapterDto);
        Chapter savedChapter = chapterRepository.save(chapter);

        log.info("Глава с названием: {} - был добавлен", savedChapter.getChapterName());

        return chapterMapper.toDto(savedChapter);
    }

    public ChapterDto updateChapter(ChapterDto chapterDto) {
        Chapter foundChapter = chapterRepository.findById(chapterDto.getId()).
                orElseThrow(() -> {
                    log.error("Глава по id: {} - не была найдена", chapterDto.getId());
                    return new RuntimeException("Глава по id: " + chapterDto.getId() + " - не была найдена");
                });
        Chapter savingChapter = chapterRepository.save(chapterMapper.toEntity(chapterDto));
        log.info("Глава по названию: {} - была обновлена", savingChapter.getChapterName());

        return chapterMapper.toDto(savingChapter);
    }


    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
        log.info("Глава с id:{} - была удалена",id);
    }


}