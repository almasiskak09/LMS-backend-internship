package kz.bitlab.MainService.serviceTests;


import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.mapper.LessonMapper;
import kz.bitlab.MainService.repository.LessonRepository;
import kz.bitlab.MainService.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonMapper lessonMapper;

    private Lesson lesson;
    private LessonDto lessonDto;
    private LocalDateTime date;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024,07,14,12,0);
        chapter = new Chapter(1L,"Основы,синтаксис языка", "Long Text",1,null,date,date,null);
        lesson = new Lesson(1L,"Мапперы", "Descripton", "Very Long Text",1,chapter,date,date);
        lessonDto = new LessonDto(1L,"Мапперы", "Descripton", "Very Long Text",1,1L,date,date);
    }

//
    @Test
    void getAllLessons() {
        List<Lesson> lessonList = List.of(
                new Lesson(1L,"Lesson1", "Desc1","Very Long Text",1,chapter,date,date),
                new Lesson(2L,"Lesson2", "Desc2","Very Long Text",1,chapter,date,date),
                new Lesson(3L,"Lesson3", "Desc3","Very Long Text",1,chapter,date,date)
        );

        List<LessonDto> lessonDtoList = List.of(
                new LessonDto(1L,"Lesson1", "Desc1","Very Long Text",1,1L,date,date),
                new LessonDto(2L,"Lesson2", "Desc2","Very Long Text",1,1L,date,date),
                new LessonDto(3L,"Lesson3", "Desc3","Very Long Text",1,1L,date,date)
        );

        when(lessonRepository.findAll()).thenReturn(lessonList);
        when(lessonMapper.toDtoList(lessonList)).thenReturn(lessonDtoList);

        List<LessonDto> result = lessonService.getAllLessons();

        assertEquals(lessonDtoList.size(), result.size());
        assertEquals(result,lessonDtoList);
        assertEquals(lessonList.get(0).getLessonName(),result.get(0).getLessonName());
        assertEquals("Lesson2", result.get(1).getLessonName());


        verify(lessonRepository, times(1)).findAll();
        verify(lessonMapper, times(1)).toDtoList(lessonList);
    }

    @Test
    void getLessonById() {
        Long id = 1L;

        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));
        when(lessonMapper.toDto(lesson)).thenReturn(lessonDto);

        LessonDto result = lessonService.getLessonById(id);

        assertEquals(lessonDto, result);
        assertEquals(lesson.getLessonName(), result.getLessonName());
        assertEquals("Мапперы", result.getLessonName());

        verify(lessonRepository, times(1)).findById(id);
        verify(lessonMapper, times(1)).toDto(lesson);

    }

    @Test
    void lessonNotFoundById_NotFoundException() {   //негативны сценарий
        Long findId = 999L;

        when(lessonRepository.findById(findId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lessonService.getLessonById(findId));

        assertEquals("Урок по id: 999 - не существует", exception.getMessage());
        verify(lessonRepository, times(1)).findById(findId);
    }


    @Test
    void getAllLessonsByChapterId() {
        Long chapterId = 1L;
        List<Lesson> lessonList = List.of(
                new Lesson(1L,"Lesson1", "Desc1","Very Long Text",1,chapter,date,date),
                new Lesson(2L,"Lesson2", "Desc2","Very Long Text",1,chapter,date,date),
                new Lesson(3L,"Lesson3", "Desc3","Very Long Text",1,chapter,date,date)
        );

        List<LessonDto> lessonDtoList = List.of(
                new LessonDto(1L,"Lesson1", "Desc1","Very Long Text",1,1L,date,date),
                new LessonDto(2L,"Lesson2", "Desc2","Very Long Text",1,1L,date,date),
                new LessonDto(3L,"Lesson3", "Desc3","Very Long Text",1,1L,date,date)
        );

        when(lessonRepository.findAllLessonsByChapterId(chapterId)).thenReturn(lessonList);
        when(lessonMapper.toDtoList(lessonList)).thenReturn(lessonDtoList);

        List<LessonDto> result = lessonService.getAllLessonsByChapterId(chapterId);

        assertEquals(lessonDtoList.size(), result.size());
        assertEquals("Lesson1", result.get(0).getLessonName());
        assertEquals("Desc2", result.get(1).getLessonDescription());
        assertEquals(lessonList.get(1).getLessonName(), result.get(1).getLessonName());

        verify(lessonRepository, times(1)).findAllLessonsByChapterId(chapterId);
        verify(lessonMapper, times(1)).toDtoList(lessonList);
    }

    @Test
    void getAllLessonsByChapterId_InvalidChapterId() {
        Long chapterId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()-> lessonService.getAllLessonsByChapterId(chapterId));
        assertEquals("Пожалуйста, укажите корректный ID главы.", exception.getMessage());
    }

    @Test
    void getAllLessonsByChapterId_EmptyLessons() {
        Long findId = 1L;
        when(lessonRepository.findAllLessonsByChapterId(findId)).thenReturn(Collections.emptyList());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> lessonService.getAllLessonsByChapterId(findId));

        assertEquals("Нет уроков по данному id главы: 1",  exception.getMessage());

    }

    @Test
    void createLesson() {

        when(lessonMapper.toEntity( lessonDto)).thenReturn(lesson);
        when(lessonRepository.save(lesson)).thenReturn(lesson);
        when(lessonMapper.toDto(lesson)).thenReturn(lessonDto);

        LessonDto result = lessonService.createLesson(lessonDto);

        assertEquals(lessonDto, result);
        assertEquals("Мапперы", result.getLessonName());
        verify(lessonRepository, times(1)).save(lesson);

    }

    @Test
    void createLesson_EmptyChapterId() {
       LessonDto emptyLessonDto = new LessonDto(1L,"Мапперы", "Descripton", "Very Long Text",1,null,date,date);

       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> lessonService.createLesson(emptyLessonDto));

       assertEquals("Пожалуйста, укажите корректный ID главы.", exception.getMessage());

    }

    @Test
    void createExistsLessonName_DataIntegrityViolationException() {

        when(lessonMapper.toEntity(lessonDto)).thenReturn(lesson);
        when(lessonRepository.save(lesson)).thenThrow(new DataIntegrityViolationException(lesson.getLessonName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> lessonService.createLesson(lessonDto));

        assertTrue(exception.getMessage().contains("Урок с таким названием уже существует: Мапперы"));

        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    void createLessonWithEmptyName_IllegalArgumentException() {
        LessonDto emptyLessonDto = new LessonDto(1L,"", "Descripton", "Very Long Text",1,1L,date,date);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> lessonService.createLesson(emptyLessonDto));
        assertEquals( exception.getMessage(),"Название урока не может быть пустым");

    }

    @Test
    void updateLesson() {

        LessonDto newLessonDto = new LessonDto(1L,"Spring", "Descripton_new", "Very Long Text",1,1L,date,date);
        Lesson newLesson = new Lesson(1L,"Spring", "Descripton_new", "Very Long Text",1,chapter,date,date);

        when(lessonRepository.findById(newLessonDto.getId())).thenReturn(Optional.of(lesson));
        when(lessonMapper.toEntity(newLessonDto)).thenReturn(newLesson);
        when(lessonRepository.save(newLesson)).thenReturn(newLesson);
        when(lessonMapper.toDto(newLesson)).thenReturn(newLessonDto);

        LessonDto result = lessonService.updateLesson(newLessonDto);

        assertEquals(result, newLessonDto);
        assertEquals("Spring", result.getLessonName());
        assertEquals(newLessonDto.getLessonContent(), result.getLessonContent());
        assertEquals("Descripton_new",result.getLessonDescription());
        assertEquals(newLessonDto.getUpdatedTime(), result.getUpdatedTime());

        verify(lessonRepository, times(1)).findById(newLessonDto.getId());
        verify(lessonRepository, times(1)).save(newLesson);
        verify(lessonMapper, times(1)).toDto(newLesson);


    }

    @Test
    void updateLessonNotFoundById_NotFoundException() {
        LessonDto fakelessonDto = new LessonDto(999L,"FakeName", "FakeDescripton", "Very Long Text",1,1L,date,date);

        when(lessonRepository.findById(fakelessonDto.getId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, ()-> lessonService.updateLesson(fakelessonDto));

        assertEquals("Урок по id: 999 - не существует", exception.getMessage());
        verify(lessonRepository, times(1)).findById(fakelessonDto.getId());
    }

    @Test
    void updateLessonWithEmptyName_IllegalArgumentException() {
        LessonDto emptyLessonDto = new LessonDto(1L,"", "Descripton", "Very Long Text",1,1L,date,date);

        when(lessonRepository.findById(emptyLessonDto.getId())).thenReturn(Optional.of(lesson));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> lessonService.updateLesson(emptyLessonDto));

        assertEquals("Название урока не может быть пустым", exception.getMessage());
    }

    @Test
    void updateLessonWithExistsName_DataIntegrityViolationException() {
        LessonDto newLessonDto = new LessonDto(1L,"Spring", "Descripton_new", "Very Long Text",1,1L,date,date);
        Lesson newLesson = new Lesson(1L,"Spring", "Descripton_new", "Very Long Text",1,chapter,date,date);

        when(lessonRepository.findById(newLessonDto.getId())).thenReturn(Optional.of(lesson));
        when(lessonMapper.toEntity(newLessonDto)).thenReturn(newLesson);
        when(lessonRepository.save(newLesson)).
                thenThrow(new DataIntegrityViolationException(newLesson.getLessonName()));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, ()->lessonService.updateLesson(newLessonDto));

        assertEquals("Урок с таким названием уже существует: Spring", exception.getMessage());

        verify(lessonRepository, times(1)).findById(newLessonDto.getId());
        verify(lessonRepository, times(1)).save(newLesson);
        verify(lessonMapper, times(1)).toEntity(newLessonDto);
    }

    @Test
    void deleteLessonById() {
        Long findId = 1L;
        when(lessonRepository.findById(findId)).thenReturn(Optional.of(lesson));

        lessonService.deleteLessonById(findId);

        verify(lessonRepository, times(1)).findById(findId);

    }

    @Test
    void deleteLessonNotFoundById_NotFoundException() {
        Long findId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, ()->lessonService.deleteLessonById(findId));

        assertEquals("Урок по id: 999 - не существует",exception.getMessage());
    }
}





