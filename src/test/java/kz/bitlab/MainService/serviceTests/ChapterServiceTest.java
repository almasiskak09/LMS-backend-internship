package kz.bitlab.MainService.serviceTests;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.ChapterMapper;
import kz.bitlab.MainService.repository.ChapterRepository;
import kz.bitlab.MainService.services.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ChapterServiceTest {

    @InjectMocks
    private ChapterService chapterService;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ChapterMapper chapterMapper;

    private Chapter chapter;
    private ChapterDto chapterDto;
    private LocalDateTime date;
    private Course course;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024,07,14,12,0);
        course = new Course(1L,"Java","1J",date,date,null);
        chapter = new Chapter(1L,"Основы,синтаксис языка","Long Text",1,course,date,date,null);
        chapterDto = new ChapterDto(1L,"Основы,синтаксис языка","Long Text",1,1L, date,date,null);
    }

    //Тест на Получение списка всех глав
    @Test
    void getAllChapters() {
        List<Chapter> chapterList = List.of(
                new Chapter(1L,"Основы,синтаксис языка","Small Long Text",1,course,date,date,null),
                new Chapter(2L,"Переменные","Medium Long Text",1,course,date,date,null),
                new Chapter(3L,"Try catch блоки", "Very Long Text",1,course,date,date,null)
        );

        List<ChapterDto> chapterDtoList = List.of(
                new ChapterDto(1L,"Основы,синтаксис языка","Small Long Text",1,1L,date,date,null),
                new ChapterDto(2L,"Переменные","Medium Long Text",1,1L,date,date,null),
                new ChapterDto(3L,"Try catch блоки","Very Long Text",1,1L,date,date,null)
        );

        when(chapterRepository.findAll()).thenReturn(chapterList);
        when(chapterMapper.toDtoList(chapterList)).thenReturn(chapterDtoList);

        List<ChapterDto> result = chapterService.getAllChapters();

        assertEquals(chapterDtoList.size(), result.size());
        assertEquals(result,chapterDtoList);
        assertEquals("Переменные", result.get(1).getChapterName());


        verify(chapterRepository, times(1)).findAll();
        verify(chapterMapper, times(1)).toDtoList(chapterList);
    }

    //Тест на Получение главы по ID
    @Test
    void getChapterById() {
        Long id = 1L;

        when(chapterRepository.findById(id)).thenReturn(Optional.of(chapter));
        when(chapterMapper.toDto(chapter)).thenReturn(chapterDto);

        ChapterDto result = chapterService.getChapterById(id);

        assertEquals(chapterDto, result);
        assertEquals("Long Text", result.getChapterDescription());

        verify(chapterRepository, times(1)).findById(id);
        verify(chapterMapper, times(1)).toDto(chapter);

    }

    //Тест на Получение главы по ID - не найден - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getChapterById_NotFoundException() {   //негативны сценарий
        Long findId = 999L;

        when(chapterRepository.findById(findId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> chapterService.getChapterById(findId));

        assertEquals("Глава курса по id: 999 - не существует", exception.getMessage());
        verify(chapterRepository, times(1)).findById(findId);
    }

    //Тест на Получение списка глав по ID курса
    @Test
    void getAllChaptersByCourseId() {
        Long courseId = 1L;
        List<Chapter> chapterList = List.of(
                new Chapter(1L, "Основы,синтаксис языка", "Small Long Text", 1, course, date, date, null),
                new Chapter(2L, "Переменные", "Medium Long Text", 1, course, date, date, null)
        );

        List<ChapterDto> chapterDtoList = List.of(
                new ChapterDto(1L, "Основы,синтаксис языка", "Small Long Text", 1, 1L, date, date, null),
                new ChapterDto(2L, "Переменные", "Medium Long Text", 1, 1L, date, date, null)
        );

        when(chapterRepository.findChaptersByCourseId(courseId)).thenReturn(chapterList);
        when(chapterMapper.toDtoList(chapterList)).thenReturn(chapterDtoList);

        List<ChapterDto> result = chapterService.getAllChaptersByCourseId(courseId);

        assertEquals(chapterDtoList.size(), result.size());
        assertEquals("Основы,синтаксис языка", result.get(0).getChapterName());
        assertEquals("Переменные", result.get(1).getChapterName());
        assertEquals(chapterList.get(1).getChapterName(), result.get(1).getChapterName());

        verify(chapterRepository, times(1)).findChaptersByCourseId(courseId);
        verify(chapterMapper, times(1)).toDtoList(chapterList);
    }

    //Тест на Получение списка глав по ID курса - некорректный ID курса - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getAllChaptersByCourseId_InvalidCourseId() {
        Long courseId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                ()-> chapterService.getAllChaptersByCourseId(courseId));
        assertEquals("Пожалуйста, укажите корректный ID курса.", exception.getMessage());
    }

    //Тест на Получение списка глав по ID курса - пустой список глав - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getAllChaptersByCourseId_EmptyChapter() {
        Long courseId = 1L;

        when(chapterRepository.findChaptersByCourseId(courseId)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,() -> chapterService.getAllChaptersByCourseId(courseId));

        assertEquals("Нет глав по id курса: 1", exception.getMessage());
    }

    //Тест на Создание главы
    @Test
    void createChapter() {

        when(chapterMapper.toEntity( chapterDto)).thenReturn(chapter);
        when(chapterRepository.save(chapter)).thenReturn(chapter);
        when(chapterMapper.toDto(chapter)).thenReturn(chapterDto);

        ChapterDto result = chapterService.createChapter(chapterDto);

        assertEquals(chapterDto, result);
        assertEquals("Основы,синтаксис языка", result.getChapterName());
        verify(chapterRepository, times(1)).save(chapter);

    }

    //Тест на создание главы, Указан неверный ID курса - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createChapter_InvalidCourseId() {
        ChapterDto emptyChapterDto = new ChapterDto(1L,"Основы,синтаксис языка","Long Text",1,null, date,date,null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> chapterService.createChapter(emptyChapterDto));

        assertEquals("Пожалуйста, укажите корректный ID курса.", exception.getMessage());
    }

    //Тест на создание главы, глава с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createExistsChapterName_DataIntegrityViolationException() {

        when(chapterMapper.toEntity(chapterDto)).thenReturn(chapter);
        when(chapterRepository.save(chapter)).thenThrow(new DataIntegrityViolationException(chapter.getChapterName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> chapterService.createChapter(chapterDto));

        assertTrue(exception.getMessage().contains("Глава с таким названием уже существует: Основы,синтаксис языка"));

        verify(chapterRepository, times(1)).save(chapter);
    }

    //Тест на создание главы, отправлено пустое название главы - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createChapterWithEmptyName_IllegalArgumentException() {
        ChapterDto emptyChapterDto = new ChapterDto(1L,"","Long Text",1,1L, date,date,null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> chapterService.createChapter(emptyChapterDto));
        assertEquals( exception.getMessage(),"Названия главы не может быть пустым");

    }

    //Тест на обновление главы
    @Test
    void updateChapter() {

        ChapterDto newChapterDto = new ChapterDto(1L,"Переменные","Medium Text",1,1L,date,date,null);
        Chapter newChapter = new Chapter(1L,"Переменные","Medium Text",1,course,date,date,null);

        when(chapterRepository.findById(newChapter.getId())).thenReturn(Optional.of(chapter));
        when(chapterMapper.toEntity(newChapterDto)).thenReturn(newChapter);
        when(chapterRepository.save(newChapter)).thenReturn(newChapter);
        when(chapterMapper.toDto(newChapter)).thenReturn(newChapterDto);

        ChapterDto result = chapterService.updateChapter(newChapterDto);

        assertEquals(result, newChapterDto);
        assertEquals("Переменные", result.getChapterName());
        assertEquals(newChapterDto.getChapterName(), result.getChapterName());
        assertEquals("Medium Text",result.getChapterDescription());
        assertEquals(newChapterDto.getUpdatedTime(), result.getUpdatedTime());

        verify(chapterRepository, times(1)).findById(newChapterDto.getId());
        verify(chapterRepository, times(1)).save(newChapter);
        verify(chapterMapper, times(1)).toEntity(newChapterDto);


    }

    //Тест на обновление главы - глава с таким Id не существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateChapterNotFoundById_NotFoundException() {
        Long findId = 999L;
        ChapterDto fakeChapterDto  = new ChapterDto(findId,"Основы,синтаксис языка","Long Text",1,1L, date,date,null);

        when(chapterRepository.findById(findId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, ()-> chapterService.updateChapter(fakeChapterDto));

        assertEquals("Глава курса по id: 999 - не существует", exception.getMessage());
        verify(chapterRepository, times(1)).findById(findId);
    }

    //Тест на обновление главы - отправлено пустое название главы - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateChapterWithEmptyName_IllegalArgumentException() {
        ChapterDto chapterDto1 = new ChapterDto(1L,"","Long Text",1,1L, date,date,null);

        when(chapterRepository.findById(chapterDto1.getId())).thenReturn(Optional.of(chapter));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> chapterService.updateChapter(chapterDto1));

        assertEquals("Названия главы не может быть пустым", exception.getMessage());
    }

    //Тест на обновление главы - глава с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateChapterWithExistsName_DataIntegrityViolationException() {

        ChapterDto newChapterDto = new ChapterDto(1L,"Web-socket","Ultra Text",1,1L,date,date,null);
        Chapter newChapter = new Chapter(1L,"Web-socket","Ultra Text",1,course,date,date,null);

        when(chapterRepository.findById(newChapterDto.getId())).thenReturn(Optional.of(chapter));
        when(chapterMapper.toEntity(newChapterDto)).thenReturn(newChapter);
        when(chapterRepository.save(newChapter)).
                thenThrow(new DataIntegrityViolationException(newChapter.getChapterName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, ()->chapterService.updateChapter(newChapterDto));

        assertEquals("Глава с таким названием уже существует: Web-socket", exception.getMessage());

        verify(chapterRepository, times(1)).findById(newChapterDto.getId());
        verify(chapterRepository, times(1)).save(newChapter);
        verify(chapterMapper, times(1)).toEntity(newChapterDto);
    }

    //Тест на удаление главы
    @Test
    void deleteChapterById() {
        Long findId = 1L;
        when(chapterRepository.findById(findId)).thenReturn(Optional.of(chapter));

        chapterService.deleteChapterById(findId);

        verify(chapterRepository, times(1)).findById(findId);
    }

    //Тест на удаление главы - глава не найдена - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void deleteChapterNotFoundById_NotFoundException() {
        Long findId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, ()->chapterService.deleteChapterById(findId));

        assertEquals("Глава курса по id: 999 - не существует",exception.getMessage());
    }
}
