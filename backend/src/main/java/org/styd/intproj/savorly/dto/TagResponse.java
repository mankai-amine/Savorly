package org.styd.intproj.savorly.dto;

import org.styd.intproj.savorly.entity.Tag;
import org.styd.intproj.savorly.dto.TagViewModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private String message;
    private List<TagViewModel> tagViewModels;

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
