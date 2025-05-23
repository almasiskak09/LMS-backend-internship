package kz.bitlab.MainService.repository;

import jakarta.transaction.Transactional;
import kz.bitlab.MainService.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    //Поиск списка уроков по ID главы, Уроки которые относятся к конкретной главе
    List<Lesson> findAllLessonsByChapterId(Long chapterId);
}
