package kz.bitlab.MainService.repository;


import jakarta.transaction.Transactional;
import kz.bitlab.MainService.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findChaptersByCourseId(Long id);
}
