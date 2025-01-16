package org.styd.intproj.savorly.repository;

import org.styd.intproj.savorly.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTitle(String title);

    @Query("SELECT t FROM Tag t WHERE t.title LIKE :title")
    List<Tag> findTagsForTitle(@Param("title") String title);

    @Query(value = "SELECT * FROM tags ORDER BY embedding <-> CAST(:embedding AS vector) LIMIT 10", nativeQuery = true)
    List<Tag> findForNearestEmbedding(@Param("embedding") float[] embedding);

}