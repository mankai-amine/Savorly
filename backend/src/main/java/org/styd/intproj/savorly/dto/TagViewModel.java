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

}

