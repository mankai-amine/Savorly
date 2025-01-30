package org.styd.intproj.savorly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.styd.intproj.savorly.entity.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByAuthorId(Long userId);

    Optional<Recipe> findByName(String name);

    @Query("SELECT r FROM Recipe r WHERE r.name LIKE :name")
    List<Recipe> findByNameLike(@Param("name") String name);

    @Query("SELECT r FROM Recipe r WHERE r.ingredients LIKE :ingredients")
    List<Recipe> findByIngredientsLike(@Param("ingredients") String ingredients);

    @Query("SELECT r FROM Recipe r WHERE r.instructions LIKE :instructions")
    List<Recipe> findByInstructionsLike(@Param("instructions") String instructions);



}

