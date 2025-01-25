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
    // no parameter constructor
    //public TagPassModel() {}

    // parameter constructor
//    public TagPassModel( String title, float[] embedding) {
//
//        this.title = title;
//        this.embedding = embedding;
//    }
//
//    // getter and setter
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public float[] getEmbedding() {
//        return embedding;
//    }
//
//    public void setEmbedding(float[] embedding) {
//        this.embedding = embedding;
//    }

}

