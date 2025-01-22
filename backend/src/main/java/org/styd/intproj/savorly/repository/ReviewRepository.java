package org.styd.intproj.savorly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.styd.intproj.savorly.entity.Review;
import org.styd.intproj.savorly.entity.Recipe;
import org.styd.intproj.savorly.entity.User;


import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.recipe.id = :recipeId")
    List<Review> getReviewsByRecipeId(Long recipeId);
}
