package kz.bitlab.MainService.repository;


import jakarta.transaction.Transactional;
import kz.bitlab.MainService.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    //Поиск списка глав по ID курса, список глав которые относятся к конкретному курсу
    List<Chapter> findChaptersByCourseId(Long id);
}
