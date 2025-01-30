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

}
