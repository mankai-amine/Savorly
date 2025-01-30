package org.styd.intproj.savorly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.styd.intproj.savorly.entity.Recipe;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {
    private String message;
    private List<Recipe> recipes;

}
