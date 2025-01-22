package org.styd.intproj.savorly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.styd.intproj.savorly.entity.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT f.rating FROM Rating f WHERE f.author.id = :userId")
    List<Rating> getRatingByUserId(Long userId);

//    @Query("SELECT f.rating FROM Rating f WHERE f.recipe.id = :recipeId")
//    List<Rating> getRatingsByRecipeId(Long recipeId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double getAverageRatingByRecipeId(Long recipeId);

    @Query("SELECT f FROM Rating f WHERE f.recipe.id = :recipeId AND f.author.id = :userId")
    Rating findByRecipeIdAndUserId(Long recipeId, Long userId);
}
