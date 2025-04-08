package kz.bitlab.MainService;

import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.mapper.ChapterMapper;
import kz.bitlab.MainService.repository.ChapterRepository;
import kz.bitlab.MainService.services.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

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
        course = new Course(1L,"Java","1J",date,date,null);

        date = LocalDateTime.of(2024,07,14,12,0);
        chapter = new Chapter(1L,"Основы,синтаксис языка","Long Text",1,course,date,date,null);
        chapterDto = new ChapterDto(1L,"Основы,синтаксис языка","Long Text",1,1L, date,date,null);
    }

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

    @Test
    void getChapterById() {

    }
}
