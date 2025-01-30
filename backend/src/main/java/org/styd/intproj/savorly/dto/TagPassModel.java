package org.styd.intproj.savorly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagPassModel {

    private String title;
    private float[] embedding;

}

