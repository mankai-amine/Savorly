package org.styd.intproj.savorly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagViewModel {
    private Long id;
    private String title;
    private String description;
    private String ingredients;

//    public TagViewModel() {}
//
//    public TagViewModel(Long id, String title, String description, String ingredients) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.ingredients = ingredients;
//    }
//
//    // Getter  Setter
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getIngredients() {
//        return ingredients;
//    }
//
//    public void setIngredients(String ingredients) {
//        this.ingredients = ingredients;
//    }


}

