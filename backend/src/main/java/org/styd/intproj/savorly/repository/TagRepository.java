package org.styd.intproj.savorly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.styd.intproj.savorly.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long> {
    //Tag findByTitle(String title);
    Optional<Tag> findByTitle(String title);

    @Query("SELECT t FROM Tag t WHERE t.title LIKE :title")
    List<Tag> findByTitleLike(@Param("title") String title);

    @Query(value = "SELECT * FROM tags ORDER BY embedding <-> CAST(:embedding AS vector) LIMIT 5", nativeQuery = true)
    List<Tag> findNearestTags(float[] embedding);

    @Modifying //@Modifying：INSERT should add a tag @Modifying，or JPA will take as SELECT
    @Transactional //ensure INSERT as a transact
    @Query(value = "INSERT INTO tags (title, ingredients, description, embedding) " +
            "VALUES (:title, :ingredients, :description, CAST(:embedding AS vector))", nativeQuery = true)
    int saveTag(@Param("title") String title,
                @Param("ingredients") String ingredients,
                @Param("description") String description,
                @Param("embedding") float[] embedding);  // use float[] directly

    @Modifying
    @Transactional
    @Query(value = "UPDATE tags SET title = :title, ingredients = :ingredients, description = :description, embedding = CAST(:embedding AS vector) WHERE id = :id", nativeQuery = true)
    int updateTag(@Param("id") Long id, @Param("title") String title, @Param("ingredients") String ingredients, @Param("description") String description, @Param("embedding") float[] embedding);


}