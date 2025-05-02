package kz.bitlab.MainService.services;

import kz.bitlab.MainService.dto.ChapterDto;
import java.util.List;


public interface ChapterService {

    //Получение списка всех глав
     List<ChapterDto> getAllChapters();

    //Получение списка глав по ID курса, список глав которые относятся к конкретному курсу
     List<ChapterDto> getAllChaptersByCourseId(Long courseId);

    //Получение главы по ID
     ChapterDto getChapterById(Long id);

    //Создание главы
     ChapterDto createChapter(ChapterDto chapterDto) ;

    //Обновление главы
     ChapterDto updateChapter(ChapterDto chapterDto) ;

    //Удаление главы по ID
     void deleteChapterById(Long id) ;





}