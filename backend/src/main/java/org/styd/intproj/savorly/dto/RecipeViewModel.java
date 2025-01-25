package org.styd.intproj.savorly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeViewModel {
    private Long id;
    private String name;
    private String ingredients;
    private String instructions;
    private String picture;
}

