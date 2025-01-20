package org.styd.intproj.savorly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.styd.intproj.savorly.entity.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findByName(String name);

    List<Recipe> findByAuthorId(Long userId);
}
