package org.styd.intproj.savorly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.styd.intproj.savorly.entity.Favourite;
import org.styd.intproj.savorly.entity.Recipe;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    @Query("SELECT f.recipe FROM Favourite f WHERE f.user.id = :userId")
    List<Recipe> getFavouriteByUserId(Long userId);

    @Query("SELECT f FROM Favourite f WHERE f.recipe.id = :recipeId AND f.user.id = :userId")
    Favourite findByRecipeIdAndUserId(Long recipeId, Long userId);
}
