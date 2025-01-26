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

//    public TagResponse(String message, List<TagViewModel> tagViewModels ) {
//        this.message = message;
//        this.tagViewModels = tagViewModels;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public List<TagViewModel> getTagViewModels() {
//        return tagViewModels;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public void setTagViewModels(List<TagViewModel> tagViewModels) {
//        this.tagViewModels = tagViewModels;
//    }
}
