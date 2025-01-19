package org.styd.intproj.savorly.entity;

import org.styd.intproj.savorly.utils.FloatArrayDeserializer;
import org.styd.intproj.savorly.utils.FloatArraySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Vector;


@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) //for mysql server
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //for postgresql
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Column(name = "ingredients")
    private String ingredients;

    @Column(name = "description")
    private String description;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 768) // dimensions
    @JsonSerialize
    @JsonDeserialize
    private float[] embedding;

    // Getter and Setter methods
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
//    public String getIngredients() {
//        return ingredients;
//    }
//
//    public void setIngredients(String ingredients) {
//        this.ingredients = ingredients;
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
//    public float[] getEmbedding() {
//        return embedding;
//    }
//
//    public void setEmbedding(float[] embedding) {
//        this.embedding = embedding;
//    }

    @OneToOne
    @JoinColumn(name = "recipe_id", unique = true)
    private Recipe recipe;

}